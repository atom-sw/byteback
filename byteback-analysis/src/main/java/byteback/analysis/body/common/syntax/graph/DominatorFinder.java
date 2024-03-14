package byteback.analysis.body.common.syntax.graph;

import byteback.analysis.common.syntax.graph.DirectedGraph;

import java.util.Collection;
import java.util.List;

/**
 * General interface for a dominators analysis.
 *
 * @author Navindra Umanee
 **/
public interface DominatorFinder<N> {
    /**
     * Returns the graph to which the analysis pertains.
     **/
    DirectedGraph<N> getGraph();

    /**
     * Returns a list of dominators for the given node in the graph.
     **/
    List<N> getDominators(N node);

    /**
     * Returns the immediate dominator of node or null if the node has no immediate dominator.
     **/
    N getImmediateDominator(N node);

    /**
     * True if "node" is dominated by "dominator" in the graph.
     **/
    boolean isDominatedBy(N node, N dominator);

    /**
     * True if "node" is dominated by all nodes in "dominators" in the graph.
     **/
    boolean isDominatedByAll(N node, Collection<N> dominators);
}