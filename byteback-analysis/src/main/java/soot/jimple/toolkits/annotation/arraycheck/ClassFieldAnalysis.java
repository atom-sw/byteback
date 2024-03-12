package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import byteback.analysis.model.ClassModel;
import byteback.analysis.model.FieldModel;
import byteback.analysis.model.MethodModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.scalar.LocalDefs;

import java.util.*;

public class ClassFieldAnalysis {
    private static final Logger logger = LoggerFactory.getLogger(ClassFieldAnalysis.class);

    public ClassFieldAnalysis(Singletons.Global g) {
    }

    public static ClassFieldAnalysis v() {
        return G.v().soot_jimple_toolkits_annotation_arraycheck_ClassFieldAnalysis();
    }

    private final boolean final_in = true;
    private final boolean private_in = true;

    /*
     * A map hold class object to other information
     *
     * SootClass --> FieldInfoTable
     */

    private final Map<ClassModel, Hashtable<FieldModel, IntValueContainer>> classToFieldInfoMap
            = new HashMap<ClassModel, Hashtable<FieldModel, IntValueContainer>>();

    protected void internalTransform(ClassModel c) {
        if (classToFieldInfoMap.containsKey(c)) {
            return;
        }

        /* Summerize class information here. */
        Date start = new Date();
        if (Options.v().verbose()) {
            logger.debug("[] ClassFieldAnalysis started on : " + start + " for " + c.getPackageName() + c.getName());
        }

        Hashtable<FieldModel, IntValueContainer> fieldInfoTable = new Hashtable<FieldModel, IntValueContainer>();
        classToFieldInfoMap.put(c, fieldInfoTable);

        /*
         * Who is the candidate for analysis? Int, Array, field. Also it should be PRIVATE now.
         */
        HashSet<FieldModel> candidSet = new HashSet<FieldModel>();

        int arrayTypeFieldNum = 0;

        Iterator<FieldModel> fieldIt = c.getFieldModels().iterator();
        while (fieldIt.hasNext()) {
            FieldModel field = fieldIt.next();
            int modifiers = field.getModifiers();

            Type type = field.getType();
            if (type instanceof ArrayType) {
                if ((final_in && ((modifiers & Modifier.FINAL) != 0)) || (private_in && ((modifiers & Modifier.PRIVATE) != 0))) {
                    candidSet.add(field);
                    arrayTypeFieldNum++;
                }
            }
        }

        if (arrayTypeFieldNum == 0) {
            if (Options.v().verbose()) {
                logger.debug("[] ClassFieldAnalysis finished with nothing");
            }
            return;
        }

        /* For FINAL field, it only needs to scan the <clinit> and <init> methods. */

        /*
         * For PRIVATE field, <clinit> is scanned to make sure that it is always assigned a value before other uses. And no other
         * assignment in other methods.
         */

        /*
         * The fastest way to determine the value of one field may get. Scan all method to get all definitions, and summerize the
         * final value. For PRIVATE STATIC field, if it is not always assigned value, it may count null pointer exception before
         * array exception
         */

        Iterator<MethodModel> methodIt = c.methodIterator();
        while (methodIt.hasNext()) {
            ScanMethod(methodIt.next(), candidSet, fieldInfoTable);
        }

        Date finish = new Date();
        if (Options.v().verbose()) {
            long runtime = finish.getTime() - start.getTime();
            long mins = runtime / 60000;
            long secs = (runtime % 60000) / 1000;
            logger.debug("[] ClassFieldAnalysis finished normally. " + "It took " + mins + " mins and " + secs + " secs.");
        }
    }

    public Object getFieldInfo(FieldModel field) {
        ClassModel c = field.getDeclaringClass();

        Map<FieldModel, IntValueContainer> fieldInfoTable = classToFieldInfoMap.get(c);

        if (fieldInfoTable == null) {
            internalTransform(c);
            fieldInfoTable = classToFieldInfoMap.get(c);
        }

        return fieldInfoTable.get(field);
    }

    /*
     * method, to be scanned candidates, the candidate set of fields, fields with value TOP are moved out of the set.
     * fieldinfo, keep the field -> value.
     */

    public void ScanMethod(MethodModel method, Set<FieldModel> candidates, Hashtable<FieldModel, IntValueContainer> fieldinfo) {
        if (!method.isConcrete()) {
            return;
        }

        Body body = method.retrieveActiveBody();

        if (body == null) {
            return;
        }

        /* no array locals, then definitely it has no array type field references. */
        {
            boolean hasArrayLocal = false;

            Collection<Local> locals = body.getLocals();

            Iterator<Local> localIt = locals.iterator();
            while (localIt.hasNext()) {
                Local local = localIt.next();
                Type type = local.getType();

                if (type instanceof ArrayType) {
                    hasArrayLocal = true;
                    break;
                }
            }

            if (!hasArrayLocal) {
                return;
            }
        }

        /* only take care of the first dimension of array size */
        /* check the assignment of fields. */

        /* Linearly scan the method body, if it has field references in candidate set. */
        /*
         * Only a.f = ... needs consideration. this.f, or other.f are treated as same because we summerize the field as a class's
         * field.
         */

        HashMap<Stmt, FieldModel> stmtfield = new HashMap<Stmt, FieldModel>();

        {
            Iterator<Unit> unitIt = body.getUnits().iterator();
            while (unitIt.hasNext()) {
                Stmt stmt = (Stmt) unitIt.next();
                if (stmt.containsFieldRef()) {
                    Value leftOp = ((AssignStmt) stmt).getLeftOp();
                    if (leftOp instanceof FieldRef fref) {
                      FieldModel field = fref.getField();

                        if (candidates.contains(field)) {
                            stmtfield.put(stmt, field);
                        }
                    }
                }
            }

            if (stmtfield.size() == 0) {
                return;
            }
        }

        if (Options.v().verbose()) {
            logger.debug("[] ScanMethod for field started.");
        }

        /* build D/U web, find the value of each candidate */
        {
            LocalDefs localDefs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(body);

            Set<Map.Entry<Stmt, FieldModel>> entries = stmtfield.entrySet();

            Iterator<Map.Entry<Stmt, FieldModel>> entryIt = entries.iterator();
            while (entryIt.hasNext()) {
                Map.Entry<Stmt, FieldModel> entry = entryIt.next();
                Stmt where = entry.getKey();
                FieldModel which = entry.getValue();

                IntValueContainer length = new IntValueContainer();

                // take out the right side of assign stmt
                Value rightOp = ((AssignStmt) where).getRightOp();

                if (rightOp instanceof Local local) {
                    // tracing down the defs of right side local.
                  DefinitionStmt usestmt = (DefinitionStmt) where;

                    while (length.isBottom()) {
                        List<Unit> defs = localDefs.getDefsOfAt(local, usestmt);
                        if (defs.size() == 1) {
                            usestmt = (DefinitionStmt) defs.get(0);

                            if (Options.v().debug()) {
                                logger.debug("        " + usestmt);
                            }

                            Value tmp_rhs = usestmt.getRightOp();
                            if ((tmp_rhs instanceof NewArrayExpr) || (tmp_rhs instanceof NewMultiArrayExpr)) {
                                Value size;

                                if (tmp_rhs instanceof NewArrayExpr) {
                                    size = ((NewArrayExpr) tmp_rhs).getSize();
                                } else {
                                    size = ((NewMultiArrayExpr) tmp_rhs).getSize(0);
                                }

                                if (size instanceof IntConstant) {
                                    length.setValue(((IntConstant) size).value);
                                } else if (size instanceof Local) {
                                    local = (Local) size;

                                    // defs = localDefs.getDefsOfAt((Local)size, (Unit)usestmt);

                                    continue;
                                } else {
                                    length.setTop();
                                }
                            } else if (tmp_rhs instanceof IntConstant) {
                                length.setValue(((IntConstant) tmp_rhs).value);
                            } else if (tmp_rhs instanceof Local) {
                                // defs = localDefs.getDefsOfAt((Local)tmp_rhs, usestmt);
                                local = (Local) tmp_rhs;

                                continue;
                            } else {
                                length.setTop();
                            }
                        } else {
                            length.setTop();
                        }
                    }
                } else {
                    /* it could be null */
                    continue;
                }

                IntValueContainer oldv = fieldinfo.get(which);

                /* the length is top, set the field to top */
                if (length.isTop()) {
                    if (oldv == null) {
                        fieldinfo.put(which, length.dup());
                    } else {
                        oldv.setTop();
                    }

                    /* remove from the candidate set. */
                    candidates.remove(which);
                } else if (length.isInteger()) {
                    if (oldv == null) {
                        fieldinfo.put(which, length.dup());
                    } else {
                        if (oldv.isInteger() && oldv.getValue() != length.getValue()) {
                            oldv.setTop();
                            candidates.remove(which);
                        }
                    }
                }
            }
        }

        if (Options.v().verbose()) {
            logger.debug("[] ScanMethod finished.");
        }
    }
}
