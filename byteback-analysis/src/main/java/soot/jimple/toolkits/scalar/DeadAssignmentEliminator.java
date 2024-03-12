package soot.jimple.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.*;
import soot.options.Options;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

import java.util.*;

public class DeadAssignmentEliminator extends BodyTransformer {
    private static final Logger logger = LoggerFactory.getLogger(DeadAssignmentEliminator.class);

    public DeadAssignmentEliminator(Singletons.Global g) {
    }

    public static DeadAssignmentEliminator v() {
        return G.v().soot_jimple_toolkits_scalar_DeadAssignmentEliminator();
    }

    /**
     * Eliminates dead code in a linear fashion. Complexity is linear with respect to the statements.
     * <p>
     * Does not work on grimp code because of the check on the right hand side for side effects.
     */
    @Override
    protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
        final boolean eliminateOnlyStackLocals = PhaseOptions.getBoolean(options, "only-stack-locals");
        final Options soptions = Options.v();
        if (soptions.verbose()) {
            logger.debug("[" + b.getMethod().getName() + "] Eliminating dead code...");
        }

        if (soptions.time()) {
            Timers.v().deadCodeTimer.start();
        }

        final Chain<Unit> units = b.getUnits();
        Deque<Unit> q = new ArrayDeque<Unit>(units.size());

        // Make a first pass through the statements, noting
        // the statements we must absolutely keep.

        boolean isStatic = b.getMethod().isStatic();
        boolean allEssential = true;
        boolean checkInvoke = false;

        Local thisLocal = null;

        for (Iterator<Unit> it = units.iterator(); it.hasNext(); ) {
            Unit s = it.next();
            boolean isEssential = true;

            if (s instanceof NopStmt) {
                // Hack: do not remove nop if is is used for a Trap
                // which is at the very end of the code.
                boolean removeNop = it.hasNext();

                if (!removeNop) {
                    removeNop = true;
                    for (Trap t : b.getTraps()) {
                        if (t.getEndUnit() == s) {
                            removeNop = false;
                            break;
                        }
                    }
                }

                if (removeNop) {
                    it.remove();
                    continue;
                }
            } else if (s instanceof AssignStmt as) {

                Value lhs = as.getLeftOp();
                Value rhs = as.getRightOp();

                // Stmt is of the form a = a which is useless
                if (lhs == rhs && lhs instanceof Local) {
                    it.remove();
                    continue;
                }

                if (lhs instanceof Local
                        && (!eliminateOnlyStackLocals || ((Local) lhs).isStackLocal() || lhs.getType() instanceof NullType)) {

                    isEssential = false;

                    if (!checkInvoke) {
                        checkInvoke = as.containsInvokeExpr();
                    }

                    if (rhs instanceof CastExpr ce) {
                        // CastExpr : can trigger ClassCastException, but null-casts never fail
                        Type t = ce.getCastType();
                        Value v = ce.getOp();
                        isEssential = !(v instanceof NullConstant) && t instanceof RefLikeType;
                    } else if (rhs instanceof InvokeExpr || rhs instanceof ArrayRef || rhs instanceof NewExpr
                            || rhs instanceof NewArrayExpr || rhs instanceof NewMultiArrayExpr || rhs instanceof LengthExpr) {
                        // ArrayRef : can have side effects (like throwing a null pointer exception)
                        // InvokeExpr : can have side effects (like throwing a null pointer exception)
                        // NewArrayExpr : can throw exception
                        // NewMultiArrayExpr : can throw exception
                        // NewExpr : can trigger class initialization
                        // LengthExpr : can throw exception
                        isEssential = true;
                    } else if (rhs instanceof FieldRef) {
                        // Can trigger class initialization
                        isEssential = true;

                        if (rhs instanceof InstanceFieldRef ifr) {

                            if (!isStatic && thisLocal == null) {
                                thisLocal = b.getThisLocal();
                            }

                            // Any InstanceFieldRef may have side effects,
                            // unless the base is reading from 'this'
                            // in a non-static method
                            isEssential = (isStatic || thisLocal != ifr.getBase());
                        }
                    } else if (rhs instanceof DivExpr || rhs instanceof RemExpr) {
                        BinopExpr expr = (BinopExpr) rhs;

                        Type t1 = expr.getOp1().getType();
                        Type t2 = expr.getOp2().getType();

                        // Can trigger a division by zero

                        boolean t2Int = t2 instanceof IntType;

                        isEssential = t2Int || t1 instanceof IntType || t1 instanceof LongType || t2 instanceof LongType
                                || t1 instanceof UnknownType || t2 instanceof UnknownType;

                        if (isEssential && t2Int) {
                            Value v = expr.getOp2();
                            if (v instanceof IntConstant i) {
                                isEssential = (i.value == 0);
                            } else {
                                isEssential = true; // could be 0, we don't know
                            }
                        }
                        if (isEssential && t2 instanceof LongType) {
                            Value v = expr.getOp2();
                            if (v instanceof LongConstant l) {
                                isEssential = (l.value == 0);
                            } else {
                                isEssential = true; // could be 0, we don't know
                            }
                        }
                    }
                }
            }

            if (isEssential) {
                q.addFirst(s);
            }

            allEssential &= isEssential;
        }

        if (checkInvoke || !allEssential) {
            // Add all the statements which are used to compute values
            // for the essential statements, recursively

            final LocalDefs localDefs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(b);

            if (!allEssential) {
                Set<Unit> essential = new HashSet<Unit>(units.size());
                while (!q.isEmpty()) {
                    Unit s = q.removeFirst();
                    if (essential.add(s)) {
                        for (ValueBox box : s.getUseBoxes()) {
                            Value v = box.getValue();
                            if (v instanceof Local l) {
                                List<Unit> defs = localDefs.getDefsOfAt(l, s);
                                if (defs != null) {
                                    q.addAll(defs);
                                }
                            }
                        }
                    }
                }
                // Remove the dead statements
                units.retainAll(essential);
            }

            if (checkInvoke) {
                final LocalUses localUses = LocalUses.Factory.newLocalUses(b, localDefs);
                // Eliminate dead assignments from invokes such as x = f(), where
                // x is no longer used

                List<AssignStmt> postProcess = new ArrayList<AssignStmt>();
                for (Unit u : units) {
                    if (u instanceof AssignStmt s) {
                        if (s.containsInvokeExpr()) {
                            // Just find one use of l which is essential
                            boolean deadAssignment = true;
                            for (UnitValueBoxPair pair : localUses.getUsesOf(s)) {
                                if (units.contains(pair.unit)) {
                                    deadAssignment = false;
                                    break;
                                }
                            }
                            if (deadAssignment) {
                                postProcess.add(s);
                            }
                        }
                    }
                }

                final Jimple jimple = Jimple.v();
                for (AssignStmt s : postProcess) {
                    // Transform it into a simple invoke.
                    Stmt newInvoke = jimple.newInvokeStmt(s.getInvokeExpr());
                    newInvoke.addAllTagsOf(s);
                    units.swapWith(s, newInvoke);

                    // If we have a callgraph, we need to fix it
                    if (Scene.v().hasCallGraph()) {
                        Scene.v().getCallGraph().swapEdgesOutOf(s, newInvoke);
                    }
                }
            }
        }
        if (soptions.time()) {
            Timers.v().deadCodeTimer.end();
        }
    }
}
