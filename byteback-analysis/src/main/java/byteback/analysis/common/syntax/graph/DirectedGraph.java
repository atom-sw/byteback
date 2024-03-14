package byteback.analysis.common.syntax.graph;

import java.util.List;
public interface DirectedGraph<N> extends Iterable<N> {
    /**
     * Returns a list of entry points for this graph.
     */
    List<N> getHeads();

    /** Returns a list of exit points for this graph. */
    List<N> getTails();

    /**
     * Returns a list of predecessors for the given node in the graph.
     */
    List<N> getPredsOf(N s);

    /**
     * Returns a list of successors for the given node in the graph.
     */
    List<N> getSuccsOf(N s);

    /**
     * Returns the node count for this graph.
     */
    int size();
}