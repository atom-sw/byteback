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
import soot.SourceLocator;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.util.dot.DotGraph;
import soot.util.dot.DotGraphEdge;
import soot.util.dot.DotGraphNode;

import java.io.File;
import java.util.*;

/**
 * Inter-procedural iterator skeleton for summary-based analysis
 * <p>
 * A "summary" is an abstract element associated to each method that fully models the effect of calling the method. In a
 * summary-based analysis, the summary of a method can be computed using solely the summary of all methods it calls: the
 * summary does not depend upon the context in which a method is called. The inter-procedural analysis interacts with a
 * intra-procedural analysis that is able to compute the summary of one method, given the summary of all the method it calls.
 * The inter-procedural analysis calls the intra-procedural analysis in a reverse topological order of method dependencies to
 * resolve unknown summaries. It iterates over recursively dependant methods.
 * <p>
 * Generally, the intra-procedural works by maintaining an abstract value that represent the effect of the method from its
 * entry point and up to the current point. At the entry point, this value is empty. The summary of the method is then the
 * merge of the abstract values at all its return points.
 * <p>
 * You can provide off-the-shelf summaries for methods you do not which to analyse. Any method using these "filtered-out"
 * methods will use the off-the-shelf summary instead of performing an intra-procedural analysis. This is useful for native
 * methods, incremental analysis, or when you hand-made summary. Methods that are called solely by filtered-out ones will
 * never be analysed, effectively trimming the call-graph dependencies.
 * <p>
 * This class tries to use the same abstract methods and data management policy as regular FlowAnalysis classes.
 *
 * @param <S>
 */
public abstract class AbstractInterproceduralAnalysis<S> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractInterproceduralAnalysis.class);

    public static final boolean doCheck = false;

    protected final CallGraph cg; // analysed call-graph
    protected final DirectedGraph<MethodModel> dg; // filtered trimed call-graph
    protected final Map<MethodModel, S> data; // SootMethod -> summary
    protected final Map<MethodModel, Integer> order; // SootMethod -> topo order
    protected final Map<MethodModel, S> unanalysed; // SootMethod -> summary

    /**
     * The constructor performs some preprocessing, but you have to call doAnalysis to preform the real stuff.
     *
     * @param cg
     * @param filter
     * @param verbose
     * @param heads
     */
    public AbstractInterproceduralAnalysis(CallGraph cg, SootMethodFilter filter, Iterator<MethodModel> heads,
                                           boolean verbose) {
        this.cg = cg;

        this.dg = new DirectedCallGraph(cg, filter, heads, verbose);
        this.data = new HashMap<MethodModel, S>();
        this.unanalysed = new HashMap<MethodModel, S>();

        // construct reverse pseudo topological order on filtered methods
        this.order = new HashMap<MethodModel, Integer>();

        int i = 0;
        for (MethodModel m : new PseudoTopologicalOrderer<MethodModel>().newList(dg, true)) {
            this.order.put(m, i);
            i++;
        }
    }

    /**
     * Initial summary value for analysed functions.
     *
     * @return
     */
    protected abstract S newInitialSummary();

    /**
     * Whenever the analyse requires the summary of a method you filtered-out, this function is called instead of
     * analyseMethod.
     *
     * <p>
     * Note: This function is called at most once per filtered-out method. It is the equivalent of entryInitialFlow!
     *
     * @param method
     * @return
     */
    protected abstract S summaryOfUnanalysedMethod(MethodModel method);

    /**
     * Compute the summary for a method by analysing its body.
     * <p>
     * Will be called only on methods not filtered-out.
     *
     * @param method is the method to be analysed
     * @param dst    is where to put the computed method summary
     */
    protected abstract void analyseMethod(MethodModel method, S dst);

    /**
     * Interprocedural analysis will call applySummary repeatedly as a consequence to
     * {@link #analyseCall(Object, Stmt, Object)}, once for each possible target method of the {@code callStmt}, provided with
     * its summary.
     *
     * @param src      summary valid before the call statement
     * @param callStmt a statement containing a InvokeStmt or InvokeExpr
     * @param summary  summary of the possible target of callStmt considered here
     * @param dst      where to put the result
     * @see analyseCall
     */
    protected abstract void applySummary(S src, Stmt callStmt, S summary, S dst);

    /**
     * Merge in1 and in2 into out.
     * <p>
     * Note: in1 or in2 can be aliased to out (e.g., analyseCall).
     *
     * @param in1
     * @param in2
     * @param out
     */
    protected abstract void merge(S in1, S in2, S out);

    /**
     * Copy src into dst.
     *
     * @param sr
     * @param dst
     */
    protected abstract void copy(S sr, S dst);

    /**
     * Called by drawAsOneDot to fill dot subgraph out with the contents of summary o.
     *
     * @param prefix gives you a unique string to prefix your node names and avoid name-clash
     * @param o
     * @param out
     */
    protected void fillDotGraph(String prefix, S o, DotGraph out) {
        throw new Error("abstract function AbstractInterproceduralAnalysis.fillDotGraph called but not implemented.");
    }

    /**
     * Analyse the call {@code callStmt} in the context {@code src}, and put the result into {@code dst}. For each possible
     * target of the call, this will get the summary for the target method (possibly
     * {@link #summaryOfUnanalysedMethod(MethodModel)}) and {@link #applySummary(Object, Stmt, Object, Object)}, then merge the
     * results into {@code dst} using {@link #merge(Object, Object, Object)}.
     *
     * @param src
     * @param dst
     * @param callStmt
     * @see #summaryOfUnanalysedMethod(MethodModel)
     * @see #applySummary(Object, Stmt, Object, Object)
     */
    public void analyseCall(S src, Stmt callStmt, S dst) {
        S accum = newInitialSummary();
        copy(accum, dst);
        // System.out.println("Edges out of " + callStmt + "...");
        for (Iterator<Edge> it = cg.edgesOutOf(callStmt); it.hasNext(); ) {
            Edge edge = it.next();
            MethodModel m = edge.tgt();
            // System.out.println("\t-> " + m.getSignature());
            S elem;
            if (data.containsKey(m)) {
                // analysed method
                elem = data.get(m);
            } else {
                // unanalysed method
                if (!unanalysed.containsKey(m)) {
                    unanalysed.put(m, summaryOfUnanalysedMethod(m));
                }
                elem = unanalysed.get(m);
            }
            applySummary(src, callStmt, elem, accum);
            merge(dst, accum, dst);
        }
    }

    /**
     * Dump the interprocedural analysis result as a graph. One node / subgraph for each analysed method that contains the
     * method summary, and call-to edges.
     * <p>
     * Note: this graph does not show filtered-out methods for which a conservative summary was asked via
     * summaryOfUnanalysedMethod.
     *
     * @param name output filename
     * @see fillDotGraph
     */
    public void drawAsOneDot(String name) {
        DotGraph dot = new DotGraph(name);
        dot.setGraphLabel(name);
        dot.setGraphAttribute("compound", "true");
        // dot.setGraphAttribute("rankdir","LR");
        int id = 0;
        Map<MethodModel, Integer> idmap = new HashMap<MethodModel, Integer>();

        // draw sub-graph cluster
        // draw sub-graph cluster
        for (MethodModel m : dg) {
            DotGraph sub = dot.createSubGraph("cluster" + id);
            DotGraphNode label = sub.drawNode("head" + id);
            idmap.put(m, id);
            sub.setGraphLabel("");
            label.setLabel("(" + order.get(m) + ") " + m.toString());
            label.setAttribute("fontsize", "18");
            label.setShape("box");
            if (data.containsKey(m)) {
                fillDotGraph("X" + id, data.get(m), sub);
            }
            id++;
        }

        // connect edges
        for (MethodModel m : dg) {
            for (MethodModel mm : dg.getSuccsOf(m)) {
                DotGraphEdge edge = dot.drawEdge("head" + idmap.get(m), "head" + idmap.get(mm));
                edge.setAttribute("ltail", "cluster" + idmap.get(m));
                edge.setAttribute("lhead", "cluster" + idmap.get(mm));
            }
        }

        File f = new File(SourceLocator.v().getOutputDir(), name + DotGraph.DOT_EXTENSION);
        dot.plot(f.getPath());
    }

    /**
     * Dump the each summary computed by the interprocedural analysis as a separate graph.
     *
     * @param prefix         is prepended before method name in output filename
     * @param drawUnanalysed do you also want info for the unanalysed methods required by the analysis via summaryOfUnanalysedMethod ?
     * @see fillDotGraph
     */
    public void drawAsManyDot(String prefix, boolean drawUnanalysed) {
        for (MethodModel m : data.keySet()) {
            DotGraph dot = new DotGraph(m.toString());
            dot.setGraphLabel(m.toString());
            fillDotGraph("X", data.get(m), dot);
            File f = new File(SourceLocator.v().getOutputDir(), prefix + m + DotGraph.DOT_EXTENSION);
            dot.plot(f.getPath());
        }

        if (drawUnanalysed) {
            for (MethodModel m : unanalysed.keySet()) {
                DotGraph dot = new DotGraph(m.toString());
                dot.setGraphLabel(m + " (unanalysed)");
                fillDotGraph("X", unanalysed.get(m), dot);
                File f = new File(SourceLocator.v().getOutputDir(), prefix + m + "_u" + DotGraph.DOT_EXTENSION);
                dot.plot(f.getPath());
            }
        }
    }

    /**
     * Query the analysis result.
     *
     * @param m
     * @return
     */
    public S getSummaryFor(MethodModel m) {
        if (data.containsKey(m)) {
            return data.get(m);
        }
        if (unanalysed.containsKey(m)) {
            return unanalysed.get(m);
        }
        return newInitialSummary();
    }

    /**
     * Get an iterator over the list of SootMethod with an associated summary. (Does not contain filtered-out or native
     * methods.)
     *
     * @return
     */
    public Iterator<MethodModel> getAnalysedMethods() {
        return data.keySet().iterator();
    }

    /**
     * Carry out the analysis.
     * <p>
     * Call this from your InterproceduralAnalysis constructor, just after super(cg). Then , you will be able to call
     * drawAsDot, for instance.
     *
     * @param verbose
     */
    protected void doAnalysis(boolean verbose) {
        // queue class
        class IntComparator implements Comparator<MethodModel> {

            @Override
            public int compare(MethodModel o1, MethodModel o2) {
                return order.get(o1) - order.get(o2);
            }
        }

        SortedSet<MethodModel> queue = new TreeSet<MethodModel>(new IntComparator());

        // init
        for (MethodModel o : order.keySet()) {
            data.put(o, newInitialSummary());
            queue.add(o);
        }

        Map<MethodModel, Integer> nb = new HashMap<MethodModel, Integer>(); // only for debug pretty-printing

        // fixpoint iterations
        while (!queue.isEmpty()) {
            MethodModel m = queue.first();
            queue.remove(m);
            S newSummary = newInitialSummary();
            S oldSummary = data.get(m);

            if (nb.containsKey(m)) {
                nb.put(m, nb.get(m) + 1);
            } else {
                nb.put(m, 1);
            }
            if (verbose) {
                logger.debug(" |- processing " + m.toString() + " (" + nb.get(m) + "-st time)");
            }

            analyseMethod(m, newSummary);
            if (!oldSummary.equals(newSummary)) {
                // summary for m changed!
                data.put(m, newSummary);
                queue.addAll(dg.getPredsOf(m));
            }
        }

        // fixpoint verification
        if (doCheck) {
            for (MethodModel m : order.keySet()) {
                S newSummary = newInitialSummary();
                S oldSummary = data.get(m);
                analyseMethod(m, newSummary);
                if (!oldSummary.equals(newSummary)) {
                    logger.debug("inter-procedural fixpoint not reached for method " + m.toString());
                    DotGraph gm = new DotGraph("false_fixpoint");
                    DotGraph gmm = new DotGraph("next_iterate");
                    gm.setGraphLabel("false fixpoint: " + m);
                    gmm.setGraphLabel("fixpoint next iterate: " + m);
                    fillDotGraph("", oldSummary, gm);
                    fillDotGraph("", newSummary, gmm);
                    gm.plot(m + "_false_fixpoint.dot");
                    gmm.plot(m + "_false_fixpoint_next.dot");
                    throw new Error("AbstractInterproceduralAnalysis sanity check failed!!!");
                }
            }
        }
    }
}
