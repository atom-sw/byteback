package soot.jimple.toolkits.ide.icfg;

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
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import heros.SynchronizedBy;
import heros.solver.IDESolver;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.pointer.LocalMustNotAliasAnalysis;
import soot.options.Options;

import java.util.*;

/**
 * This is an implementation of AbstractJimpleBasedICFG that computes the ICFG on-the-fly. In other words, it can be used
 * without pre-computing a call graph. Instead this implementation resolves calls through Class Hierarchy Analysis (CHA), as
 * implemented through FastHierarchy. The CHA is supported by LocalMustNotAliasAnalysis, which is used to determine cases
 * where the concrete type of an object at an InvokeVirtual or InvokeInterface callsite is known. In these cases the call can
 * be resolved concretely, i.e., to a single target.
 * <p>
 * To be sound, for InvokeInterface calls that cannot be resolved concretely, OnTheFlyJimpleBasedICFG requires that all
 * classes on the classpath be loaded at least to signatures. This must be done before the FastHierarchy is computed such
 * that the hierarchy is intact. Clients should call {@link #loadAllClassesOnClassPathToSignatures()} to load all required
 * classes to this level.
 *
 * @see FastHierarchy#resolveConcreteDispatch(ClassModel, MethodModel)
 * @see FastHierarchy#resolveAbstractDispatch(ClassModel, MethodModel)
 */
public class OnTheFlyJimpleBasedICFG extends AbstractJimpleBasedICFG {

    @SynchronizedBy("by use of synchronized LoadingCache class")
    protected final LoadingCache<Body, LocalMustNotAliasAnalysis> bodyToLMNAA
            = IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<Body, LocalMustNotAliasAnalysis>() {
        @Override
        public LocalMustNotAliasAnalysis load(Body body) throws Exception {
            return new LocalMustNotAliasAnalysis(getOrCreateUnitGraph(body), body);
        }
    });

    @SynchronizedBy("by use of synchronized LoadingCache class")
    protected final LoadingCache<Unit, Set<MethodModel>> unitToCallees
            = IDESolver.DEFAULT_CACHE_BUILDER.build(new CacheLoader<Unit, Set<MethodModel>>() {
        @Override
        public Set<MethodModel> load(Unit u) throws Exception {
            Stmt stmt = (Stmt) u;
            InvokeExpr ie = stmt.getInvokeExpr();
            FastHierarchy fastHierarchy = Scene.v().getFastHierarchy();
            // FIXME Handle Thread.start etc.
            if (ie instanceof InstanceInvokeExpr) {
                if (ie instanceof SpecialInvokeExpr) {
                    // special
                    return Collections.singleton(ie.getMethod());
                } else {
                    // virtual and interface
                    InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
                    Local base = (Local) iie.getBase();
                    RefType concreteType = bodyToLMNAA.getUnchecked(getBodyOf(u)).concreteType(base, stmt);
                    if (concreteType != null) {
                        // the base variable definitely points to a single concrete type
                        MethodModel singleTargetMethod
                                = fastHierarchy.resolveConcreteDispatch(concreteType.getSootClass(), iie.getMethod());
                        return Collections.singleton(singleTargetMethod);
                    } else {
                        ClassModel baseTypeClass;
                        if (base.getType() instanceof RefType refType) {
                          baseTypeClass = refType.getSootClass();
                        } else if (base.getType() instanceof ArrayType) {
                            baseTypeClass = Scene.v().getSootClass(Scene.v().getObjectType().toString());
                        } else if (base.getType() instanceof NullType) {
                            // if the base is definitely null then there is no call target
                            return Collections.emptySet();
                        } else {
                            throw new InternalError("Unexpected base type:" + base.getType());
                        }
                        return fastHierarchy.resolveAbstractDispatch(baseTypeClass, iie.getMethod());
                    }
                }
            } else {
                // static
                return Collections.singleton(ie.getMethod());
            }
        }
    });

    @SynchronizedBy("explicit lock on data structure")
    protected Map<MethodModel, Set<Unit>> methodToCallers = new HashMap<MethodModel, Set<Unit>>();

    public OnTheFlyJimpleBasedICFG(MethodModel... entryPoints) {
        this(Arrays.asList(entryPoints));
    }

    public OnTheFlyJimpleBasedICFG(Collection<MethodModel> entryPoints) {
        for (MethodModel m : entryPoints) {
            initForMethod(m);
        }
    }

    protected Body initForMethod(MethodModel m) {
        assert Scene.v().hasFastHierarchy();
        Body b = null;
        if (m.isConcrete()) {
            ClassModel declaringClass = m.getDeclaringClass();
            ensureClassHasBodies(declaringClass);
            synchronized (Scene.v()) {
                b = m.retrieveActiveBody();
            }
            if (b != null) {
                for (Unit u : b.getUnits()) {
                    if (!setOwnerStatement(u, b)) {
                        // if the unit was registered already then so were all units;
                        // simply skip the rest
                        break;
                    }
                }
            }
        }
        assert Scene.v().hasFastHierarchy();
        return b;
    }

    private synchronized void ensureClassHasBodies(ClassModel cl) {
        assert Scene.v().hasFastHierarchy();
        if (cl.getResolvingLevel() < ClassModel.BODIES) {
            Scene.v().forceResolve(cl.getName(), ClassModel.BODIES);
            Scene.v().getOrMakeFastHierarchy();
        }
        assert Scene.v().hasFastHierarchy();
    }

    @Override
    public Set<MethodModel> getCalleesOfCallAt(Unit u) {
        Set<MethodModel> targets = unitToCallees.getUnchecked(u);
        for (MethodModel m : targets) {
            addCallerForMethod(u, m);
            initForMethod(m);
        }
        return targets;
    }

    private void addCallerForMethod(Unit callSite, MethodModel target) {
        synchronized (methodToCallers) {
            Set<Unit> callers = methodToCallers.get(target);
            if (callers == null) {
                callers = new HashSet<Unit>();
                methodToCallers.put(target, callers);
            }
            callers.add(callSite);
        }
    }

    @Override
    public Set<Unit> getCallersOf(MethodModel m) {
        Set<Unit> callers = methodToCallers.get(m);
        return callers == null ? Collections.emptySet() : callers;

        // throw new UnsupportedOperationException("This class is not suited for unbalanced problems");
    }

    public static void loadAllClassesOnClassPathToSignatures() {
        for (String path : SourceLocator.explodeClassPath(Scene.v().getSootClassPath())) {
            for (String cl : SourceLocator.v().getClassesUnder(path)) {
                Scene.v().forceResolve(cl, ClassModel.SIGNATURES);
            }
        }
    }

    public static void main(String[] args) {
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.onflyicfg", new SceneTransformer() {

            @Override
            protected void internalTransform(String phaseName, Map<String, String> options) {
                if (Scene.v().hasCallGraph()) {
                    throw new RuntimeException("call graph present!");
                }

                loadAllClassesOnClassPathToSignatures();

                MethodModel mainMethod = Scene.v().getMainMethod();
                OnTheFlyJimpleBasedICFG icfg = new OnTheFlyJimpleBasedICFG(mainMethod);
                Set<MethodModel> worklist = new LinkedHashSet<MethodModel>();
                Set<MethodModel> visited = new HashSet<MethodModel>();
                worklist.add(mainMethod);
                int monomorphic = 0, polymorphic = 0;
                while (!worklist.isEmpty()) {
                    Iterator<MethodModel> iter = worklist.iterator();
                    MethodModel currMethod = iter.next();
                    iter.remove();
                    visited.add(currMethod);
                    System.err.println(currMethod);
                    // MUST call this method to initialize ICFG for every method
                    Body body = currMethod.getActiveBody();
                    if (body == null) {
                        continue;
                    }
                    for (Unit u : body.getUnits()) {
                        Stmt s = (Stmt) u;
                        if (s.containsInvokeExpr()) {
                            Set<MethodModel> calleesOfCallAt = icfg.getCalleesOfCallAt(s);
                            if (s.getInvokeExpr() instanceof VirtualInvokeExpr || s.getInvokeExpr() instanceof InterfaceInvokeExpr) {
                                if (calleesOfCallAt.size() <= 1) {
                                    monomorphic++;
                                } else {
                                    polymorphic++;
                                }
                                System.err.println("mono: " + monomorphic + "   poly: " + polymorphic);
                            }
                            for (MethodModel callee : calleesOfCallAt) {
                                if (!visited.contains(callee)) {
                                    System.err.println(callee);
                                    // worklist.add(callee);
                                }
                            }
                        }
                    }
                }
            }

        }));
        Options.v().set_on_the_fly(true);
        soot.Main.main(args);
    }

}
