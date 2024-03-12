package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Filter;
import soot.jimple.toolkits.callgraph.TransitiveTargets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Generates side-effect information from a PointsToAnalysis.
 */
public class SideEffectAnalysis {

    private final Map<MethodModel, MethodRWSet> methodToNTReadSet = new HashMap<MethodModel, MethodRWSet>();
    private final Map<MethodModel, MethodRWSet> methodToNTWriteSet = new HashMap<MethodModel, MethodRWSet>();
    private final PointsToAnalysis pa;
    private final CallGraph cg;
    private final TransitiveTargets tt;

    private SideEffectAnalysis(PointsToAnalysis pa, CallGraph cg, TransitiveTargets tt) {
        if (G.v().Union_factory == null) {
            G.v().Union_factory = new UnionFactory() {
                @Override
                public Union newUnion() {
                    return FullObjectSet.v();
                }
            };
        }
        this.pa = pa;
        this.cg = cg;
        this.tt = tt;
    }

    public SideEffectAnalysis(PointsToAnalysis pa, CallGraph cg) {
        this(pa, cg, new TransitiveTargets(cg));
    }

    public SideEffectAnalysis(PointsToAnalysis pa, CallGraph cg, Filter filter) {
        // This constructor allows customization of call graph edges to
        // consider via the use of a transitive targets filter.
        // For example, using the NonClinitEdgesPred, you can create a
        // SideEffectAnalysis that will ignore static initializers
        // - R. Halpert 2006-12-02
        this(pa, cg, new TransitiveTargets(cg, filter));
    }

    public void findNTRWSets(MethodModel method) {
        if (methodToNTReadSet.containsKey(method) && methodToNTWriteSet.containsKey(method)) {
            return;
        }

        MethodRWSet read = null;
        MethodRWSet write = null;
        for (Unit next : method.retrieveActiveBody().getUnits()) {
            final Stmt s = (Stmt) next;
            RWSet ntr = ntReadSet(method, s);
            if (ntr != null) {
                if (read == null) {
                    read = new MethodRWSet();
                }
                read.union(ntr);
            }
            RWSet ntw = ntWriteSet(method, s);
            if (ntw != null) {
                if (write == null) {
                    write = new MethodRWSet();
                }
                write.union(ntw);
            }
        }
        methodToNTReadSet.put(method, read);
        methodToNTWriteSet.put(method, write);
    }

    public RWSet nonTransitiveReadSet(MethodModel method) {
        findNTRWSets(method);
        return methodToNTReadSet.get(method);
    }

    public RWSet nonTransitiveWriteSet(MethodModel method) {
        findNTRWSets(method);
        return methodToNTWriteSet.get(method);
    }

    private RWSet ntReadSet(MethodModel method, Stmt stmt) {
        if (stmt instanceof AssignStmt) {
            return addValue(((AssignStmt) stmt).getRightOp(), method, stmt);
        } else {
            return null;
        }
    }

    public RWSet readSet(MethodModel method, Stmt stmt) {
        RWSet ret = null;
        for (Iterator<MethodOrMethodContext> targets = tt.iterator(stmt); targets.hasNext(); ) {
            MethodModel target = (MethodModel) targets.next();
            if (target.isNative()) {
                if (ret == null) {
                    ret = new SiteRWSet();
                }
                ret.setCallsNative();
            } else if (target.isConcrete()) {
                RWSet ntr = nonTransitiveReadSet(target);
                if (ntr != null) {
                    if (ret == null) {
                        ret = new SiteRWSet();
                    }
                    ret.union(ntr);
                }
            }
        }
        if (ret == null) {
            return ntReadSet(method, stmt);
        } else {
            ret.union(ntReadSet(method, stmt));
            return ret;
        }
    }

    private RWSet ntWriteSet(MethodModel method, Stmt stmt) {
        if (stmt instanceof AssignStmt) {
            return addValue(((AssignStmt) stmt).getLeftOp(), method, stmt);
        } else {
            return null;
        }
    }

    public RWSet writeSet(MethodModel method, Stmt stmt) {
        RWSet ret = null;
        for (Iterator<MethodOrMethodContext> targets = tt.iterator(stmt); targets.hasNext(); ) {
            MethodModel target = (MethodModel) targets.next();
            if (target.isNative()) {
                if (ret == null) {
                    ret = new SiteRWSet();
                }
                ret.setCallsNative();
            } else if (target.isConcrete()) {
                RWSet ntw = nonTransitiveWriteSet(target);
                if (ntw != null) {
                    if (ret == null) {
                        ret = new SiteRWSet();
                    }
                    ret.union(ntw);
                }
            }
        }
        if (ret == null) {
            return ntWriteSet(method, stmt);
        } else {
            ret.union(ntWriteSet(method, stmt));
            return ret;
        }
    }

    protected RWSet addValue(Value v, MethodModel m, Stmt s) {
        RWSet ret = null;
        if (v instanceof InstanceFieldRef ifr) {
            PointsToSet base = pa.reachingObjects((Local) ifr.getBase());
            ret = new StmtRWSet();
            ret.addFieldRef(base, ifr.getField());
        } else if (v instanceof StaticFieldRef sfr) {
            ret = new StmtRWSet();
            ret.addGlobal(sfr.getField());
        } else if (v instanceof ArrayRef ar) {
            PointsToSet base = pa.reachingObjects((Local) ar.getBase());
            ret = new StmtRWSet();
            ret.addFieldRef(base, PointsToAnalysis.ARRAY_ELEMENTS_NODE);
        }
        return ret;
    }

    @Override
    public String toString() {
        return "SideEffectAnalysis: PA=" + pa + " CG=" + cg;
    }
}
