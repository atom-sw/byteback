package byteback.analysis.body.common.syntax.graph;

import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.common.syntax.stmt.Unit;

import java.util.HashMap;
import java.util.List;

public class BriefUnitGraph extends UnitGraph {

    /**
     * Constructs a BriefUnitGraph given a Body instance.
     *
     * @param body
     *          The underlying body we want to make a graph for.
     */
    public BriefUnitGraph(Body body) {
        super(body);
        int size = unitChain.size();

        unitToSuccs = new HashMap<>(size * 2 + 1, 0.7f);
        unitToPreds = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);
        buildUnexceptionalEdges(unitToSuccs, unitToPreds);
        buildHeadsAndTails();
    }

}