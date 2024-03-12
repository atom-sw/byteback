package soot.jimple.toolkits.invoke;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
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
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.scalar.LocalNameStandardizer;
import soot.options.SMBOptions;
import soot.util.Chain;

import java.util.*;

/**
 * Uses the Scene's currently-active InvokeGraph to statically bind monomorphic call sites.
 */
public class StaticMethodBinder extends SceneTransformer {

    public StaticMethodBinder(Singletons.Global g) {
    }

    public static StaticMethodBinder v() {
        return G.v().soot_jimple_toolkits_invoke_StaticMethodBinder();
    }

    @Override
    protected void internalTransform(String phaseName, Map<String, String> opts) {
        final Filter instanceInvokesFilter = new Filter(new InstanceInvokeEdgesPred());
        final SMBOptions options = new SMBOptions(opts);
        final String modifierOptions = PhaseOptions.getString(opts, "allowed-modifier-changes");
        final HashMap<MethodModel, MethodModel> instanceToStaticMap = new HashMap<MethodModel, MethodModel>();

        final Scene scene = Scene.v();
        final CallGraph cg = scene.getCallGraph();
        final Hierarchy hierarchy = scene.getActiveHierarchy();

        for (ClassModel c : scene.getApplicationClasses()) {
            LinkedList<MethodModel> methodsList = new LinkedList<MethodModel>();
            for (Iterator<MethodModel> it = c.methodIterator(); it.hasNext(); ) {
                MethodModel next = it.next();
                methodsList.add(next);
            }

            while (!methodsList.isEmpty()) {
                MethodModel container = methodsList.removeFirst();
                if (!container.isConcrete() || !instanceInvokesFilter.wrap(cg.edgesOutOf(container)).hasNext()) {
                    continue;
                }

                final Body b = container.getActiveBody();
                final Chain<Unit> bUnits = b.getUnits();
                for (Unit u : new ArrayList<Unit>(bUnits)) {
                    final Stmt s = (Stmt) u;
                    if (!s.containsInvokeExpr()) {
                        continue;
                    }

                    final InvokeExpr ie = s.getInvokeExpr();
                    if (ie instanceof StaticInvokeExpr || ie instanceof SpecialInvokeExpr) {
                        continue;
                    }

                    final Targets targets = new Targets(instanceInvokesFilter.wrap(cg.edgesOutOf(s)));
                    if (!targets.hasNext()) {
                        continue;
                    }
                    final MethodModel target = (MethodModel) targets.next();
                    // Ok, we have an Interface or VirtualInvoke going to 1.
                    if (targets.hasNext() || !AccessManager.ensureAccess(container, target, modifierOptions)) {
                        continue;
                    }
                    final ClassModel targetDeclClass = target.getDeclaringClass();
                    if (!targetDeclClass.isApplicationClass() || !target.isConcrete()) {
                        continue;
                    }

                    // Don't modify java.lang.Object
                    if (targetDeclClass == scene.getSootClass(Scene.v().getObjectType().toString())) {
                        continue;
                    }

                    if (!instanceToStaticMap.containsKey(target)) {
                        List<Type> newParameterTypes = new ArrayList<Type>();
                        newParameterTypes.add(RefType.v(targetDeclClass.getName()));
                        newParameterTypes.addAll(target.getParameterTypes());

                        // Check for signature conflicts.
                        String newName = target.getName();
                        do {
                            newName = newName + "_static";
                        } while (targetDeclClass.declaresMethod(newName, newParameterTypes, target.getReturnType()));

                        MethodModel ct = scene.makeSootMethod(newName, newParameterTypes, target.getReturnType(),
                                target.getModifiers() | Modifier.STATIC, target.getExceptions());
                        targetDeclClass.addMethod(ct);

                        methodsList.addLast(ct);

                        final Body ctBody = (Body) target.getActiveBody().clone();
                        ct.setActiveBody(ctBody);

                        // Make the invoke graph take into account the
                        // newly-cloned body.
                        {
                            Iterator<Unit> oldUnits = target.getActiveBody().getUnits().iterator();
                            for (Unit newStmt : ctBody.getUnits()) {
                                Unit oldStmt = oldUnits.next();
                                for (Iterator<Edge> edges = cg.edgesOutOf(oldStmt); edges.hasNext(); ) {
                                    Edge e = edges.next();
                                    cg.addEdge(new Edge(ct, newStmt, e.tgt(), e.kind()));
                                    cg.removeEdge(e);
                                }
                            }
                        }

                        // Shift the parameter list to apply to the new this parameter.
                        // If the method uses this, then we replace
                        // the r0 := @this with r0 := @parameter0 & shift.
                        // Otherwise, just zap the r0 := @this.
                        {
                            final Chain<Unit> ctBodyUnits = ctBody.getUnits();
                            for (Iterator<Unit> unitsIt = ctBodyUnits.snapshotIterator(); unitsIt.hasNext(); ) {
                                Stmt st = (Stmt) unitsIt.next();
                                if (st instanceof IdentityStmt is) {
                                  Value rightOp = is.getRightOp();
                                    if (rightOp instanceof ThisRef) {
                                        final Jimple jimp = Jimple.v();
                                        ctBodyUnits.swapWith(st,
                                                jimp.newIdentityStmt(is.getLeftOp(), jimp.newParameterRef(rightOp.getType(), 0)));
                                    } else if (rightOp instanceof ParameterRef ro) {
                                      ro.setIndex(ro.getIndex() + 1);
                                    }
                                }
                            }

                        }

                        instanceToStaticMap.put(target, ct);
                    }

                    final Value invokeBase = ((InstanceInvokeExpr) ie).getBase();
                    Value thisToAdd = invokeBase;

                    // Insert casts to please the verifier.
                    if (options.insert_redundant_casts()) {
                        // The verifier will complain if targetUsesThis, and:
                        // the argument passed to the method is not the same type.
                        // For instance, Bottle.price_static takes a cost.
                        // Cost is an interface implemented by Bottle.
                        ClassModel localType = ((RefType) invokeBase.getType()).getSootClass();
                        if (localType.isInterface() || hierarchy.isClassSuperclassOf(localType, targetDeclClass)) {
                            final Jimple jimp = Jimple.v();
                            RefType targetDeclClassType = targetDeclClass.getClassType();
                            Local castee = jimp.newLocal("__castee", targetDeclClassType);
                            b.getLocals().add(castee);
                            bUnits.insertBefore(jimp.newAssignStmt(castee, jimp.newCastExpr(invokeBase, targetDeclClassType)), s);
                            thisToAdd = castee;
                        }
                    }

                    final MethodModel clonedTarget = instanceToStaticMap.get(target);

                    // Now rebind the method call & fix the invoke graph.
                    {
                        List<Value> newArgs = new ArrayList<Value>();
                        newArgs.add(thisToAdd);
                        newArgs.addAll(ie.getArgs());

                        s.getInvokeExprBox().setValue(Jimple.v().newStaticInvokeExpr(clonedTarget.makeRef(), newArgs));
                        cg.addEdge(new Edge(container, s, clonedTarget));
                    }

                    // (If enabled), add a null pointer check.
                    if (options.insert_null_checks()) {
                        final Jimple jimp = Jimple.v();
                        /* Ah ha. Caught again! */
                        if (TrapManager.isExceptionCaughtAt(scene.getSootClass("java.lang.NullPointerException"), s, b)) {
                            /*
                             * In this case, we don't use throwPoint; instead, put the code right there.
                             */
                            IfStmt insertee = jimp.newIfStmt(jimp.newNeExpr(invokeBase, NullConstant.v()), s);

                            bUnits.insertBefore(insertee, s);

                            // This sucks (but less than before).
                            insertee.setTarget(s);

                            ThrowManager.addThrowAfter(b.getLocals(), bUnits, insertee);
                        } else {
                            bUnits.insertBefore(jimp.newIfStmt(jimp.newEqExpr(invokeBase, NullConstant.v()),
                                    ThrowManager.getNullPointerExceptionThrower(b)), s);
                        }
                    }

                    // Add synchronizing stuff.
                    if (target.isSynchronized()) {
                        clonedTarget.setModifiers(clonedTarget.getModifiers() & ~Modifier.SYNCHRONIZED);
                        SynchronizerManager.v().synchronizeStmtOn(s, b, (Local) invokeBase);
                    }

                    // Resolve name collisions.
                    LocalNameStandardizer.v().transform(b, phaseName + ".lns");
                }
            }
        }
    }
}
