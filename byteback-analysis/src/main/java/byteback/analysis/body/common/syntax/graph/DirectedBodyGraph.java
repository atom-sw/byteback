package byteback.analysis.body.common.syntax.graph;

import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.common.syntax.graph.DirectedGraph;

public interface DirectedBodyGraph<T> extends DirectedGraph<T> {

    Body getBody();
}
