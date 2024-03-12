package soot.jimple.toolkits.annotation.purity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Antoine Mine
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
import byteback.analysis.model.MethodModel;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.DirectedGraph;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

import java.util.*;

/**
 * Builds a DirectedGraph from a CallGraph and SootMethodFilter.
 * <p>
 * This is used in AbstractInterproceduralAnalysis to construct a reverse pseudo topological order on which to iterate. You
 * can specify a SootMethodFilter to trim the graph by cutting call edges.
 * <p>
 * Methods filtered-out by the SootMethodFilter will not appear in the DirectedGraph!
 */
public class DirectedCallGraph implements DirectedGraph<MethodModel> {
    private static final Logger logger = LoggerFactory.getLogger(DirectedCallGraph.class);

    protected Set<MethodModel> nodes;
    protected Map<MethodModel, List<MethodModel>> succ;
    protected Map<MethodModel, List<MethodModel>> pred;
    protected List<MethodModel> heads;
    protected List<MethodModel> tails;
    protected int size;

    /**
     * The constructor does all the work here. After constructed, you can safely use all interface methods. Moreover, these
     * methods should perform very fast...
     * <p>
     * The DirectedGraph will only contain methods in call paths from a method in head and comprising only methods wanted by
     * filter. Moreover, only concrete methods are put in the graph...
     *
     * @param cg
     * @param filter
     * @param heads   is a List of SootMethod
     * @param verbose
     */
    public DirectedCallGraph(CallGraph cg, SootMethodFilter filter, Iterator<MethodModel> heads, boolean verbose) {
        // filter heads by filter
        List<MethodModel> filteredHeads = new LinkedList<MethodModel>();
        while (heads.hasNext()) {
            MethodModel m = heads.next();
            if (m.isConcrete() && filter.want(m)) {
                filteredHeads.add(m);
            }
        }

        this.nodes = new HashSet<MethodModel>(filteredHeads);

        MultiMap<MethodModel, MethodModel> s = new HashMultiMap<MethodModel, MethodModel>();
        MultiMap<MethodModel, MethodModel> p = new HashMultiMap<MethodModel, MethodModel>();

        if (verbose) {
            logger.debug("[AM] dumping method dependencies");
        }
        // simple breadth-first visit
        int nb = 0;
        Set<MethodModel> remain = new HashSet<MethodModel>(filteredHeads);
        while (!remain.isEmpty()) {
            Set<MethodModel> newRemain = new HashSet<MethodModel>();
            for (MethodModel m : remain) {
                if (verbose) {
                    logger.debug(" |- " + m.toString() + " calls");
                }

                for (Iterator<Edge> itt = cg.edgesOutOf(m); itt.hasNext(); ) {
                    Edge edge = itt.next();
                    MethodModel mm = edge.tgt();
                    boolean keep = mm.isConcrete() && filter.want(mm);
                    if (verbose) {
                        logger.debug(" |  |- " + mm + (keep ? "" : " (filtered out)"));
                    }
                    if (keep) {
                        if (this.nodes.add(mm)) {
                            newRemain.add(mm);
                        }
                        s.put(m, mm);
                        p.put(mm, m);
                    }
                }
                nb++;
            }
            remain = newRemain;
        }
        logger.debug("[AM] number of methods to be analysed: " + nb);

        // MultiMap -> Map of List
        this.succ = new HashMap<MethodModel, List<MethodModel>>();
        this.pred = new HashMap<MethodModel, List<MethodModel>>();
        this.tails = new LinkedList<MethodModel>();
        this.heads = new LinkedList<MethodModel>();
        for (MethodModel x : this.nodes) {
            Set<MethodModel> ss = s.get(x);
            Set<MethodModel> pp = p.get(x);
            this.succ.put(x, new LinkedList<MethodModel>(ss));
            this.pred.put(x, new LinkedList<MethodModel>(pp));
            if (ss.isEmpty()) {
                this.tails.add(x);
            }
            if (pp.isEmpty()) {
                this.heads.add(x);
            }
        }

        this.size = this.nodes.size();
    }

    /**
     * You get a List of SootMethod.
     *
     * @return
     */
    @Override
    public List<MethodModel> getHeads() {
        return heads;
    }

    /**
     * You get a List of SootMethod.
     *
     * @return
     */
    @Override
    public List<MethodModel> getTails() {
        return tails;
    }

    /**
     * You get an Iterator on SootMethod.
     *
     * @return
     */
    @Override
    public Iterator<MethodModel> iterator() {
        return nodes.iterator();
    }

    /**
     * @return
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * You get a List of SootMethod.
     *
     * @param s
     * @return
     */
    @Override
    public List<MethodModel> getSuccsOf(MethodModel s) {
        return succ.get(s);
    }

    /**
     * You get a List of SootMethod.
     *
     * @param s
     * @return
     */
    @Override
    public List<MethodModel> getPredsOf(MethodModel s) {
        return pred.get(s);
    }
}
