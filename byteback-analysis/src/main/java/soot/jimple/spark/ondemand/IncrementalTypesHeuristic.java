package soot.jimple.spark.ondemand;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
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

import soot.RefType;
import byteback.analysis.model.FieldModel;
import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.ondemand.genericutil.Util;
import soot.jimple.spark.ondemand.pautil.SootUtil;
import soot.jimple.spark.ondemand.pautil.SootUtil.CallSiteAndContext;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.SparkField;

import java.util.HashSet;
import java.util.Set;

public class IncrementalTypesHeuristic implements FieldCheckHeuristic {

    private final TypeManager manager;

    private static final boolean EXCLUDE_TYPES = false;

    private static final String[] EXCLUDED_NAMES = new String[]{"ca.mcgill.sable.soot.SootMethod"};

    private final Set<RefType> typesToCheck = new HashSet<RefType>();

    private final Set<RefType> notBothEndsTypes = new HashSet<RefType>();

    private RefType newTypeOnQuery = null;

    /*
     * (non-Javadoc)
     *
     * @see AAA.algs.Heuristic#newQuery()
     */
    public boolean runNewPass() {
        // if (!aggressive && reachedAggressive) {
        // aggressive = true;
        // return true;
        // }
        if (newTypeOnQuery != null) {
            boolean added = typesToCheck.add(newTypeOnQuery);
            if (SootUtil.hasRecursiveField(newTypeOnQuery.getSootClass())) {
                notBothEndsTypes.add(newTypeOnQuery);
            }
            newTypeOnQuery = null;
            return added;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see AAA.algs.Heuristic#validateMatchesForField(soot.jimple.spark.pag.SparkField)
     */
    public boolean validateMatchesForField(SparkField field) {
        // if (true) return true;
        if (field instanceof ArrayElement) {
            return true;
        }
        FieldModel fieldModel = (FieldModel) field;
        RefType declaringType = fieldModel.getDeclaringClass().getClassType();
        if (EXCLUDE_TYPES) {
            for (String typeName : EXCLUDED_NAMES) {
                if (Util.stringContains(declaringType.toString(), typeName)) {
                    return false;
                }
            }
        }
        for (RefType typeToCheck : typesToCheck) {
            if (manager.castNeverFails(declaringType, typeToCheck)) {
                return true;
            }
        }
        if (newTypeOnQuery == null) {
            newTypeOnQuery = declaringType;
            // System.err.println("adding type " + declaringType);
        }
        // System.err.println("false for " + field);
        return false;
    }

    public IncrementalTypesHeuristic(TypeManager manager) {
        super();
        this.manager = manager;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append("types ");
        ret.append(typesToCheck.toString());
        if (!notBothEndsTypes.isEmpty()) {
            ret.append(" not both ");
            ret.append(notBothEndsTypes);
        }
        return ret.toString();
    }

    public boolean validFromBothEnds(SparkField field) {
        if (field instanceof FieldModel fieldModel) {
            RefType declaringType = fieldModel.getDeclaringClass().getClassType();
            for (RefType type : notBothEndsTypes) {
                if (manager.castNeverFails(declaringType, type)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean refineVirtualCall(CallSiteAndContext callSiteAndContext) {
        // TODO make real heuristic
        return true;
    }

}
