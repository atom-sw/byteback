package soot.toolkits.scalar;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.tag.*;

import java.util.*;
import java.util.Map.Entry;

/**
 * This is the reverse operation of the {@link ConstantValueToInitializerTransformer}. We scan for {@code <clinit>} methods
 * that initialize a final field with a constant value and create a {@link ConstantValueTag} from this value. Afterwards, the
 * assignment in the {@code <clinit>} method is removed. If {@code <clinit>} runs empty, it is deleted as well.
 *
 * @author Steven Arzt
 */
public class ConstantInitializerToTagTransformer extends SceneTransformer {
    private static final Logger logger = LoggerFactory.getLogger(ConstantInitializerToTagTransformer.class);
    private static final ConstantInitializerToTagTransformer INSTANCE = new ConstantInitializerToTagTransformer();

    public static ConstantInitializerToTagTransformer v() {
        return INSTANCE;
    }

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        for (ClassModel sc : Scene.v().getClasses()) {
            transformClass(sc, false);
        }
    }

    /**
     * Transforms the given class, i.e. scans for a {@code <clinit>} method and generates new constant value tags for all
     * constant assignments to static final fields.
     *
     * @param sc                The class to transform
     * @param removeAssignments True if the assignments inside the {@code <clinit>} method shall be removed, otherwise false
     */
    public void transformClass(ClassModel sc, boolean removeAssignments) {
        // If this class has no <clinit> method, we're done
        MethodModel smInit = sc.getMethodByNameUnsafe("<clinit>");
        if (smInit == null || !smInit.isConcrete()) {
            return;
        }

        Set<FieldModel> nonConstantFields = new HashSet<FieldModel>();
        Map<FieldModel, ConstantValueTag> newTags = new HashMap<FieldModel, ConstantValueTag>();
        // in case of mismatch between code/constant table values, constant tags are removed
        Set<FieldModel> removeTagList = new HashSet<FieldModel>();
        for (Iterator<Unit> itU = smInit.getActiveBody().getUnits().snapshotIterator(); itU.hasNext(); ) {
            Unit u = itU.next();
            if (u instanceof AssignStmt assign) {
              final Value leftOp = assign.getLeftOp();
                if (leftOp instanceof StaticFieldRef) {
                    final Value rightOp = assign.getRightOp();
                    if (rightOp instanceof Constant) {
                        FieldModel field = null;
                        try {
                            field = ((StaticFieldRef) leftOp).getField();
                            if (field == null || nonConstantFields.contains(field)) {
                                continue;
                            }
                        } catch (ConflictingFieldRefException ex) {
                            // Ignore this statement
                            continue;
                        }

                        if (field.getDeclaringClass().equals(sc) && field.isStatic() && field.isFinal()) {
                            // Do we already have a constant value for this field?
                            boolean found = false;
                            for (Tag t : field.getTags()) {
                                if (t instanceof ConstantValueTag) {
                                    if (checkConstantValue((ConstantValueTag) t, (Constant) rightOp)) {
                                        // If we assign the same value we also have
                                        // in the constant table, we can get rid of
                                        // the assignment.
                                        if (removeAssignments) {
                                            itU.remove();
                                        }
                                    } else {
                                        logger.debug("WARNING: Constant value for field '" + field + "' mismatch between code (" + rightOp
                                                + ") and constant table (" + t + ")");
                                        removeTagList.add(field);
                                    }
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                // If we already have a different tag for this field, the
                                // value is not constant and we do not associate the tags.
                                if (!checkConstantValue(newTags.get(field), (Constant) rightOp)) {
                                    nonConstantFields.add(field);
                                    newTags.remove(field);
                                    removeTagList.add(field);
                                    continue;
                                }

                                ConstantValueTag newTag = createConstantTagFromValue((Constant) rightOp);
                                if (newTag != null) {
                                    newTags.put(field, newTag);
                                }
                            }
                        }
                    } else {
                        // a non-constant is assigned to the field
                        try {
                            FieldModel sf = ((StaticFieldRef) leftOp).getField();
                            if (sf != null) {
                                removeTagList.add(sf);
                            }
                        } catch (ConflictingFieldRefException ex) {
                            // let's assume that a broken field doesn't cause any harm
                        }
                    }
                }
            }
        }

        // Do the actual assignment
        for (Entry<FieldModel, ConstantValueTag> entry : newTags.entrySet()) {
            FieldModel field = entry.getKey();
            if (!removeTagList.contains(field)) {
                field.addTag(entry.getValue());
            }
        }

        if (removeAssignments && !newTags.isEmpty()) {
            for (Iterator<Unit> itU = smInit.getActiveBody().getUnits().snapshotIterator(); itU.hasNext(); ) {
                Unit u = itU.next();
                if (u instanceof AssignStmt) {
                    final Value leftOp = ((AssignStmt) u).getLeftOp();
                    if (leftOp instanceof FieldRef) {
                        try {
                            FieldModel fld = ((FieldRef) leftOp).getField();
                            if (fld != null && newTags.containsKey(fld)) {
                                itU.remove();
                            }
                        } catch (ConflictingFieldRefException ex) {
                            // Ignore broken code
                        }
                    }
                }
            }
        }

        // remove constant tags
        for (FieldModel sf : removeTagList) {
            if (removeTagList.contains(sf)) {
                List<Tag> toRemoveTagList = new ArrayList<Tag>();
                for (Tag t : sf.getTags()) {
                    if (t instanceof ConstantValueTag) {
                        toRemoveTagList.add(t);
                    }
                }
                for (Tag t : toRemoveTagList) {
                    sf.getTags().remove(t);
                }
            }
        }
    }

    private ConstantValueTag createConstantTagFromValue(Constant rightOp) {
        if (rightOp instanceof DoubleConstant) {
            return new DoubleConstantValueTag(((DoubleConstant) rightOp).value);
        } else if (rightOp instanceof FloatConstant) {
            return new FloatConstantValueTag(((FloatConstant) rightOp).value);
        } else if (rightOp instanceof IntConstant) {
            return new IntegerConstantValueTag(((IntConstant) rightOp).value);
        } else if (rightOp instanceof LongConstant) {
            return new LongConstantValueTag(((LongConstant) rightOp).value);
        } else if (rightOp instanceof StringConstant) {
            return new StringConstantValueTag(((StringConstant) rightOp).value);
        } else {
            return null;
        }
    }

    private boolean checkConstantValue(ConstantValueTag t, Constant rightOp) {
        if (t == null || rightOp == null) {
            return true;
        }

        if (t instanceof DoubleConstantValueTag) {
            return (rightOp instanceof DoubleConstant)
                    && (((DoubleConstantValueTag) t).getDoubleValue() == ((DoubleConstant) rightOp).value);
        } else if (t instanceof FloatConstantValueTag) {
            return (rightOp instanceof FloatConstant)
                    && (((FloatConstantValueTag) t).getFloatValue() == ((FloatConstant) rightOp).value);
        } else if (t instanceof IntegerConstantValueTag) {
            return (rightOp instanceof IntConstant)
                    && (((IntegerConstantValueTag) t).getIntValue() == ((IntConstant) rightOp).value);
        } else if (t instanceof LongConstantValueTag) {
            return (rightOp instanceof LongConstant)
                    && (((LongConstantValueTag) t).getLongValue() == ((LongConstant) rightOp).value);
        } else if (t instanceof StringConstantValueTag) {
            return (rightOp instanceof StringConstant)
                    && ((StringConstantValueTag) t).getStringValue().equals(((StringConstant) rightOp).value);
        } else {
            // We don't know the type, so we assume it's alright
            return true;
        }
    }
}
