package byteback.analysis.body.common.syntax.graph;

import byteback.analysis.body.common.syntax.Body;

public class BriefBlockGraph extends BlockGraph {
    /**
     * Constructs a {@link BriefBlockGraph} from a given {@link Body}.
     *
     * <p>
     * Note that this constructor builds a {@link BriefUnitGraph} internally when splitting <tt>body</tt>'s {@link Unit}s into
     * {@link Block}s. Callers who already have a {@link BriefUnitGraph} to hand can use the constructor taking a
     * <tt>CompleteUnitGraph</tt> as a parameter, as a minor optimization.
     *
     * @param body
     *          the {@link Body} for which to build a graph.
     */
    public BriefBlockGraph(final Body body) {
        this(new BriefUnitGraph(body));
    }

    /**
     * Constructs a {@link BriefBlockGraph} representing the <tt>Unit</tt>-level control flow represented by the passed
     * {@link BriefUnitGraph}.
     *
     * @param unitGraph
     *          the {@link Body} for which to build a graph.
     */
    public BriefBlockGraph(BriefUnitGraph unitGraph) {
        super(unitGraph);
    }
}
