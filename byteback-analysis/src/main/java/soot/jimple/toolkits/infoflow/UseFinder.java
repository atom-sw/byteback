package soot.jimple.toolkits.infoflow;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

import java.util.*;

// UseFinder written by Richard L. Halpert, 2007-03-13
// Compiles a list of all uses of fields of each application class within the
// application classes by looking at every application method.
// Compiles a list of all calls to methods of each application class within the
// application classes by using the call graph.

public class UseFinder {
    ReachableMethods rm;

    Map<ClassModel, List> classToExtFieldAccesses; // each field access is a Pair <containing method, stmt>
    Map<ClassModel, ArrayList> classToIntFieldAccesses;

    Map<ClassModel, List> classToExtCalls; // each call is a Pair <containing method, stmt>
    Map<ClassModel, ArrayList> classToIntCalls;

    public UseFinder() {
        classToExtFieldAccesses = new HashMap<ClassModel, List>();
        classToIntFieldAccesses = new HashMap<ClassModel, ArrayList>();
        classToExtCalls = new HashMap<ClassModel, List>();
        classToIntCalls = new HashMap<ClassModel, ArrayList>();

        rm = Scene.v().getReachableMethods();

        doAnalysis();
    }

    public UseFinder(ReachableMethods rm) {
        classToExtFieldAccesses = new HashMap<ClassModel, List>();
        classToIntFieldAccesses = new HashMap<ClassModel, ArrayList>();
        classToExtCalls = new HashMap<ClassModel, List>();
        classToIntCalls = new HashMap<ClassModel, ArrayList>();

        this.rm = rm;

        doAnalysis();
    }

    public List getExtFieldAccesses(ClassModel sc) {
        if (classToExtFieldAccesses.containsKey(sc)) {
            return classToExtFieldAccesses.get(sc);
        }
        throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
    }

    public List getIntFieldAccesses(ClassModel sc) {
        if (classToIntFieldAccesses.containsKey(sc)) {
            return classToIntFieldAccesses.get(sc);
        }
        throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
    }

    public List getExtCalls(ClassModel sc) {
        if (classToExtCalls.containsKey(sc)) {
            return classToExtCalls.get(sc);
        }
        throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
    }

    public List getIntCalls(ClassModel sc) {
        if (classToIntCalls.containsKey(sc)) {
            return classToIntCalls.get(sc);
        }
        throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
    }

    // This is an incredibly stupid way to do this... we should just use the call graph for faster/better info!
    public List<MethodModel> getExtMethods(ClassModel sc) {
        if (classToExtCalls.containsKey(sc)) {
            List extCalls = classToExtCalls.get(sc);
            List<MethodModel> extMethods = new ArrayList<MethodModel>();
            for (Iterator callIt = extCalls.iterator(); callIt.hasNext(); ) {
                Pair call = (Pair) callIt.next();
                MethodModel calledMethod = ((Stmt) call.getO2()).getInvokeExpr().getMethod();
                if (!extMethods.contains(calledMethod)) {
                    extMethods.add(calledMethod);
                }
            }
            return extMethods;
        }
        throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
    }

    public List<FieldModel> getExtFields(ClassModel sc) {
        if (classToExtFieldAccesses.containsKey(sc)) {
            List extAccesses = classToExtFieldAccesses.get(sc);
            List<FieldModel> extFields = new ArrayList<FieldModel>();
            for (Iterator accessIt = extAccesses.iterator(); accessIt.hasNext(); ) {
                Pair access = (Pair) accessIt.next();
                FieldModel accessedField = ((Stmt) access.getO2()).getFieldRef().getField();
                if (!extFields.contains(accessedField)) {
                    extFields.add(accessedField);
                }
            }
            return extFields;
        }
        throw new RuntimeException("UseFinder does not search non-application classes: " + sc);
    }

    private void doAnalysis() {
        Chain appClasses = Scene.v().getApplicationClasses();

        // Set up lists of internal and external accesses
        Iterator appClassesIt = appClasses.iterator();
        while (appClassesIt.hasNext()) {
            ClassModel appClass = (ClassModel) appClassesIt.next();
            classToIntFieldAccesses.put(appClass, new ArrayList());
            classToExtFieldAccesses.put(appClass, new ArrayList());
            classToIntCalls.put(appClass, new ArrayList());
            classToExtCalls.put(appClass, new ArrayList());
        }

        // Find internal and external accesses
        appClassesIt = appClasses.iterator();
        while (appClassesIt.hasNext()) {
            ClassModel appClass = (ClassModel) appClassesIt.next();
            Iterator methodsIt = appClass.getMethodModels().iterator();
            while (methodsIt.hasNext()) {
                MethodModel method = (MethodModel) methodsIt.next();
                if (method.isConcrete() && rm.contains(method)) {
                    Body b = method.retrieveActiveBody();
                    Iterator unitsIt = b.getUnits().iterator();
                    while (unitsIt.hasNext()) {
                        Stmt s = (Stmt) unitsIt.next();
                        if (s.containsFieldRef()) {
                            FieldRef fr = s.getFieldRef();
                            if (fr.getFieldRef().resolve().getDeclaringClass() == appClass) {
                                if (fr instanceof StaticFieldRef) {
                                    // static field ref in same class is considered internal
                                    classToIntFieldAccesses.get(appClass).add(new Pair(method, s));
                                } else if (fr instanceof InstanceFieldRef ifr) {
                                  if (!method.isStatic() && ifr.getBase().equivTo(b.getThisLocal())) {
                                        // this.field ref is considered internal
                                        classToIntFieldAccesses.get(appClass).add(new Pair(method, s));
                                    } else {
                                        // o.field ref is considered external
                                        classToExtFieldAccesses.get(appClass).add(new Pair(method, s));
                                    }
                                }
                            } else {
                                // ref to some other class is considered external
                                List<Pair> otherClassList = classToExtFieldAccesses.get(fr.getFieldRef().resolve().getDeclaringClass());
                                if (otherClassList == null) {
                                    otherClassList = new ArrayList<Pair>();
                                    classToExtFieldAccesses.put(fr.getFieldRef().resolve().getDeclaringClass(), otherClassList);
                                }
                                otherClassList.add(new Pair(method, s));
                            }
                        }
                        if (s.containsInvokeExpr()) {
                            InvokeExpr ie = s.getInvokeExpr();
                            if (ie.getMethodRef().resolve().getDeclaringClass() == appClass) // what about sub/superclasses
                            {
                                if (ie instanceof StaticInvokeExpr) {
                                    // static field ref in same class is considered internal
                                    classToIntCalls.get(appClass).add(new Pair(method, s));
                                } else if (ie instanceof InstanceInvokeExpr iie) {
                                  if (!method.isStatic() && iie.getBase().equivTo(b.getThisLocal())) {
                                        // this.field ref is considered internal
                                        classToIntCalls.get(appClass).add(new Pair(method, s));
                                    } else {
                                        // o.field ref is considered external
                                        classToExtCalls.get(appClass).add(new Pair(method, s));
                                    }
                                }
                            } else {
                                // ref to some other class is considered external
                                List<Pair> otherClassList = classToExtCalls.get(ie.getMethodRef().resolve().getDeclaringClass());
                                if (otherClassList == null) {
                                    otherClassList = new ArrayList<Pair>();
                                    classToExtCalls.put(ie.getMethodRef().resolve().getDeclaringClass(), otherClassList);
                                }
                                otherClassList.add(new Pair(method, s));
                            }
                        }
                    }
                }
            }
        }
    }
}
