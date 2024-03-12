package soot.jimple.validation;

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
import byteback.analysis.model.MethodModel;
import soot.*;
import soot.jimple.*;
import soot.validation.BodyValidator;
import soot.validation.ValidationException;

import java.util.List;

public enum InvokeValidator implements BodyValidator {
    INSTANCE;

    public static InvokeValidator v() {
        return INSTANCE;
    }

    @Override
    public void validate(Body body, List<ValidationException> exceptions) {
        final ClassModel objClass = Scene.v().getObjectType().getSootClass();
        for (Unit unit : body.getUnits()) {
            if (unit instanceof Stmt statement) {
              if (statement.containsInvokeExpr()) {
                    final InvokeExpr ie = statement.getInvokeExpr();
                    final SootMethodRef methodRef = ie.getMethodRef();
                    try {
                        final MethodModel method = methodRef.resolve();
                        if (!method.isPhantom()) {
                            if (method.isStaticInitializer()) {
                                exceptions.add(new ValidationException(unit, "Calling <clinit> methods is not allowed."));
                            } else if (method.isStatic()) {
                                if (!(ie instanceof StaticInvokeExpr)) {
                                    exceptions.add(new ValidationException(unit, "Should use staticinvoke for static methods."));
                                }
                            } else {
                                final ClassModel clazzDeclaring = method.getDeclaringClass();
                                if (clazzDeclaring.isInterface()) {
                                    if (!(ie instanceof InterfaceInvokeExpr)) {
                                        // There are cases where the Java bytecode verifier allows an
                                        // invokevirtual or invokespecial to target an interface method.
                                        if (!(ie instanceof VirtualInvokeExpr || ie instanceof SpecialInvokeExpr)) {
                                            exceptions.add(new ValidationException(unit,
                                                    "Should use interface/virtual/specialinvoke for interface methods."));
                                        }
                                    }
                                } else if (method.isPrivate() || method.isConstructor()) {
                                    if (!(ie instanceof SpecialInvokeExpr)) {
                                        String type = method.isPrivate() ? "private methods" : "constructors";
                                        exceptions.add(new ValidationException(unit, "Should use specialinvoke for " + type + "."));
                                    }
                                } else if (methodRef.getDeclaringClass().isInterface() && objClass.equals(clazzDeclaring)) {
                                    // invokeinterface can be used to invoke the base Object methods
                                    if (!(ie instanceof InterfaceInvokeExpr || ie instanceof VirtualInvokeExpr
                                            || ie instanceof SpecialInvokeExpr)) {
                                        exceptions.add(new ValidationException(unit,
                                                "Should use interface/virtual/specialinvoke for java.lang.Object methods."));
                                    }
                                } else {
                                    // NOTE: beyond constructors, there's not a rule to separate
                                    // super.X from this.X because there exist scenarios where it
                                    // is valid to use the exact same references with either a
                                    // specialinvoke or a virtualinvoke. Consider classes A and B
                                    // where B extends A. Both classes define a method "void m()".
                                    // It is legal for a method in B to have either of the these:
                                    // - virtualinvoke this.<A: void m()>() //i.e. ((A)this).m()
                                    // - specialinvoke this.<A: void m()>() //i.e. super.m()
                                    // Both are valid bytecode (although their behavior differs).
                                    if (!(ie instanceof VirtualInvokeExpr || ie instanceof SpecialInvokeExpr)) {
                                        exceptions.add(new ValidationException(unit, "Should use virtualinvoke or specialinvoke."));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Error on resolving
                    }
                }
            }
        }
    }

    @Override
    public boolean isBasicValidator() {
        return false;
    }
}
