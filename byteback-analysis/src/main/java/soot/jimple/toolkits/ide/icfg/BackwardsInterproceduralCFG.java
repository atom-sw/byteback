package soot.jimple.toolkits.ide.icfg;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2013 Eric Bodden and others
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
import soot.Unit;
import soot.Value;
import soot.toolkits.graph.DirectedGraph;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Same as {@link JimpleBasedInterproceduralCFG} but based on inverted unit graphs. This should be used for backward
 * analyses.
 */
public class BackwardsInterproceduralCFG implements BiDiInterproceduralCFG<Unit, MethodModel> {

    protected final BiDiInterproceduralCFG<Unit, MethodModel> delegate;

    public BackwardsInterproceduralCFG(BiDiInterproceduralCFG<Unit, MethodModel> fwICFG) {
        delegate = fwICFG;
    }

    // swapped
    @Override
    public List<Unit> getSuccsOf(Unit n) {
        return delegate.getPredsOf(n);
    }

    // swapped
    @Override
    public Collection<Unit> getStartPointsOf(MethodModel m) {
        return delegate.getEndPointsOf(m);
    }

    // swapped
    @Override
    public List<Unit> getReturnSitesOfCallAt(Unit n) {
        return delegate.getPredsOfCallAt(n);
    }

    // swapped
    @Override
    public boolean isExitStmt(Unit stmt) {
        return delegate.isStartPoint(stmt);
    }

    // swapped
    @Override
    public boolean isStartPoint(Unit stmt) {
        return delegate.isExitStmt(stmt);
    }

    // swapped
    @Override
    public Set<Unit> allNonCallStartNodes() {
        return delegate.allNonCallEndNodes();
    }

    // swapped
    @Override
    public List<Unit> getPredsOf(Unit u) {
        return delegate.getSuccsOf(u);
    }

    // swapped
    @Override
    public Collection<Unit> getEndPointsOf(MethodModel m) {
        return delegate.getStartPointsOf(m);
    }

    // swapped
    @Override
    public List<Unit> getPredsOfCallAt(Unit u) {
        return delegate.getSuccsOf(u);
    }

    // swapped
    @Override
    public Set<Unit> allNonCallEndNodes() {
        return delegate.allNonCallStartNodes();
    }

    // same
    @Override
    public MethodModel getMethodOf(Unit n) {
        return delegate.getMethodOf(n);
    }

    // same
    @Override
    public Collection<MethodModel> getCalleesOfCallAt(Unit n) {
        return delegate.getCalleesOfCallAt(n);
    }

    // same
    @Override
    public Collection<Unit> getCallersOf(MethodModel m) {
        return delegate.getCallersOf(m);
    }

    // same
    @Override
    public Set<Unit> getCallsFromWithin(MethodModel m) {
        return delegate.getCallsFromWithin(m);
    }

    // same
    @Override
    public boolean isCallStmt(Unit stmt) {
        return delegate.isCallStmt(stmt);
    }

    // same
    @Override
    public DirectedGraph<Unit> getOrCreateUnitGraph(MethodModel m) {
        return delegate.getOrCreateUnitGraph(m);
    }

    // same
    @Override
    public List<Value> getParameterRefs(MethodModel m) {
        return delegate.getParameterRefs(m);
    }

    @Override
    public boolean isFallThroughSuccessor(Unit stmt, Unit succ) {
        throw new UnsupportedOperationException("not implemented because semantics unclear");
    }

    @Override
    public boolean isBranchTarget(Unit stmt, Unit succ) {
        throw new UnsupportedOperationException("not implemented because semantics unclear");
    }

    // swapped
    @Override
    public boolean isReturnSite(Unit n) {
        for (Unit pred : getSuccsOf(n)) {
            if (isCallStmt(pred)) {
                return true;
            }
        }
        return false;
    }

    // same
    @Override
    public boolean isReachable(Unit u) {
        return delegate.isReachable(u);
    }

}
