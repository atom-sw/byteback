package soot.jimple.toolkits.annotation.methods;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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
import byteback.analysis.model.MethodModel;
import soot.*;
import soot.tag.ColorTag;
import soot.tag.StringTag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * A scene transformer that adds tags to unused methods.
 */
public class UnreachableMethodsTagger extends SceneTransformer {
    public UnreachableMethodsTagger(Singletons.Global g) {
    }

    public static UnreachableMethodsTagger v() {
        return G.v().soot_jimple_toolkits_annotation_methods_UnreachableMethodsTagger();
    }

    protected void internalTransform(String phaseName, Map options) {

        // make list of all unreachable methods
        ArrayList<MethodModel> methodList = new ArrayList<MethodModel>();

        Iterator getClassesIt = Scene.v().getApplicationClasses().iterator();
        while (getClassesIt.hasNext()) {
            ClassModel appClass = (ClassModel) getClassesIt.next();

            Iterator getMethodsIt = appClass.getMethodModels().iterator();
            while (getMethodsIt.hasNext()) {
                MethodModel method = (MethodModel) getMethodsIt.next();
                // System.out.println("adding method: "+method);
                if (!Scene.v().getReachableMethods().contains(method)) {
                    methodList.add(method);
                }
            }
        }

        // tag unused methods
        Iterator<MethodModel> unusedIt = methodList.iterator();
        while (unusedIt.hasNext()) {
            MethodModel unusedMethod = unusedIt.next();
            unusedMethod.addTag(new StringTag("Method " + unusedMethod.getName() + " is not reachable!", "Unreachable Methods"));
            unusedMethod.addTag(new ColorTag(255, 0, 0, true, "Unreachable Methods"));
            // System.out.println("tagged method: "+unusedMethod);

        }
    }

}
