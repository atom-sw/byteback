package soot.jimple.toolkits.pointer.nativemethods;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Feng Qian
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

import byteback.analysis.model.MethodModel;
import soot.jimple.toolkits.pointer.representations.Environment;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;
import soot.jimple.toolkits.pointer.util.NativeHelper;

public class JavaUtilTimeZoneNative extends NativeMethodClass {
    public JavaUtilTimeZoneNative(NativeHelper helper) {
        super(helper);
    }

    /**
     * Implements the abstract method simulateMethod. It distributes the request to the corresponding methods by signatures.
     */
    public void simulateMethod(MethodModel method, ReferenceVariable thisVar, ReferenceVariable returnVar,
                               ReferenceVariable[] params) {

        String subSignature = method.getSubSignature();

        if (subSignature.equals("java.lang.String getSystemTimeZoneID(java.lang.String,java.lang.String)")) {
            java_util_TimeZone_getSystemTimeZoneID(method, thisVar, returnVar, params);

        } else {
            defaultMethod(method, thisVar, returnVar, params);

        }
    }

    /*********************** java.util.TimeZone **********************/
    /**
     * It should return a constant for TimeZone
     * <p>
     * Gets the TimeZone for the given ID.
     * <p>
     * private static native java.lang.String getSystemTimeZoneID(java.lang.String, java.lang.String);
     */
    public void java_util_TimeZone_getSystemTimeZoneID(MethodModel method, ReferenceVariable thisVar,
                                                       ReferenceVariable returnVar, ReferenceVariable[] params) {
        helper.assignObjectTo(returnVar, Environment.v().getStringObject());
    }
}
