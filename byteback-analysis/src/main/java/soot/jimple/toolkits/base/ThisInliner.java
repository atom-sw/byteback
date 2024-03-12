package soot.jimple.toolkits.base;

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

import byteback.analysis.model.MethodModel;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.shimple.ShimpleBody;
import soot.util.Chain;

import java.util.HashMap;
import java.util.Map;

public class ThisInliner extends BodyTransformer {

    private static final boolean DEBUG = false;

    @Override
    public void internalTransform(Body b, String phaseName, Map<String, String> options) {
        assert (b instanceof JimpleBody || b instanceof ShimpleBody);

        // Ensure body is a constructor
        if (!"<init>".equals(b.getMethod().getName())) {
            return;
        }

        // If the first invoke is a this() and not a super() inline the this()
        final InvokeStmt invokeStmt = getFirstSpecialInvoke(b);
        if (invokeStmt == null) {
            return;
        }
        final SpecialInvokeExpr specInvokeExpr = (SpecialInvokeExpr) invokeStmt.getInvokeExpr();
        final MethodModel specInvokeMethod = specInvokeExpr.getMethod();
        if (specInvokeMethod.getDeclaringClass().equals(b.getMethod().getDeclaringClass())) {
            // Get or construct the body for the method
            final Body specInvokeBody = specInvokeMethod.retrieveActiveBody();
            assert (b.getClass() == specInvokeBody.getClass());

            // Put locals from inlinee into container
            HashMap<Local, Local> oldLocalsToNew = new HashMap<Local, Local>();
            for (Local l : specInvokeBody.getLocals()) {
                Local newLocal = (Local) l.clone();
                b.getLocals().add(newLocal);
                oldLocalsToNew.put(l, newLocal);
            }
            if (DEBUG) {
                System.out.println("locals: " + b.getLocals());
            }

            // Find @this identity stmt of original method
            final Value origIdStmtLHS = findIdentityStmt(b).getLeftOp();

            final HashMap<Unit, Unit> oldStmtsToNew = new HashMap<Unit, Unit>();
            final Chain<Unit> containerUnits = b.getUnits();
            for (Unit u : specInvokeBody.getUnits()) {
                Stmt inlineeStmt = (Stmt) u;

                if (inlineeStmt instanceof IdentityStmt idStmt) {
                    // Handle identity stmts
                    final Value rightOp = idStmt.getRightOp();

                    if (rightOp instanceof ThisRef) {
                        Stmt newThis = Jimple.v().newAssignStmt(oldLocalsToNew.get((Local) idStmt.getLeftOp()), origIdStmtLHS);
                        containerUnits.insertBefore(newThis, invokeStmt);
                        oldStmtsToNew.put(inlineeStmt, newThis);
                    } else if (rightOp instanceof CaughtExceptionRef) {
                        Stmt newInlinee = (Stmt) inlineeStmt.clone();
                        for (ValueBox vb : newInlinee.getUseAndDefBoxes()) {
                            Value val = vb.getValue();
                            if (val instanceof Local) {
                                vb.setValue(oldLocalsToNew.get((Local) val));
                            }
                        }
                        containerUnits.insertBefore(newInlinee, invokeStmt);
                        oldStmtsToNew.put(inlineeStmt, newInlinee);
                    } else if (rightOp instanceof ParameterRef) {
                        Stmt newParam = Jimple.v().newAssignStmt(oldLocalsToNew.get((Local) idStmt.getLeftOp()),
                                specInvokeExpr.getArg(((ParameterRef) rightOp).getIndex()));
                        containerUnits.insertBefore(newParam, invokeStmt);
                        oldStmtsToNew.put(inlineeStmt, newParam);
                    }
                } else if (inlineeStmt instanceof ReturnVoidStmt) {
                    // Handle return void stmts (cannot return anything else from a constructor)
                    Stmt newRet = Jimple.v().newGotoStmt(containerUnits.getSuccOf(invokeStmt));
                    containerUnits.insertBefore(newRet, invokeStmt);
                    if (DEBUG) {
                        System.out.println("adding to stmt map: " + inlineeStmt + " and " + newRet);
                    }
                    oldStmtsToNew.put(inlineeStmt, newRet);
                } else {
                    Stmt newInlinee = (Stmt) inlineeStmt.clone();
                    for (ValueBox vb : newInlinee.getUseAndDefBoxes()) {
                        Value val = vb.getValue();
                        if (val instanceof Local) {
                            vb.setValue(oldLocalsToNew.get((Local) val));
                        }
                    }
                    containerUnits.insertBefore(newInlinee, invokeStmt);
                    oldStmtsToNew.put(inlineeStmt, newInlinee);
                }
            }

            // handleTraps
            for (Trap t : specInvokeBody.getTraps()) {
                Unit newBegin = oldStmtsToNew.get(t.getBeginUnit());
                Unit newEnd = oldStmtsToNew.get(t.getEndUnit());
                Unit newHandler = oldStmtsToNew.get(t.getHandlerUnit());
                if (DEBUG) {
                    System.out.println("begin: " + t.getBeginUnit());
                    System.out.println("end: " + t.getEndUnit());
                    System.out.println("handler: " + t.getHandlerUnit());
                }
                if (newBegin == null || newEnd == null || newHandler == null) {
                    throw new RuntimeException("couldn't map trap!");
                }
                b.getTraps().add(Jimple.v().newTrap(t.getException(), newBegin, newEnd, newHandler));
            }

            // patch gotos
            for (Unit u : specInvokeBody.getUnits()) {
                if (u instanceof GotoStmt inlineeStmt) {
                    if (DEBUG) {
                        System.out.println("inlinee goto target: " + inlineeStmt.getTarget());
                    }
                    ((GotoStmt) oldStmtsToNew.get(inlineeStmt)).setTarget(oldStmtsToNew.get(inlineeStmt.getTarget()));
                }
            }

            // remove original invoke
            containerUnits.remove(invokeStmt);

            // resolve name collisions
            LocalNameStandardizer.v().transform(b, "ji.lns");
        }
        if (DEBUG) {
            System.out.println("locals: " + b.getLocals());
            System.out.println("units: " + b.getUnits());
        }
    }

    private InvokeStmt getFirstSpecialInvoke(Body b) {
        for (Unit u : b.getUnits()) {
            if (u instanceof InvokeStmt s) {
                if (s.getInvokeExpr() instanceof SpecialInvokeExpr) {
                    return s;
                }
            }
        }
        // but there will always be either a call to this() or to super() from the constructor
        return null;
    }

    private IdentityStmt findIdentityStmt(Body b) {
        for (Unit u : b.getUnits()) {
            if (u instanceof IdentityStmt s) {
                if (s.getRightOp() instanceof ThisRef) {
                    return s;
                }
            }
        }
        return null;
    }
}
