package byteback.analysis.body.common.syntax.graph;

import byteback.analysis.common.syntax.graph.DirectedGraph;

import java.util.*;

public class MHGDominatorFinder<N> implements DominatorFinder<N> {

    protected final DirectedGraph<N> graph;

    protected final Set<N> heads;

    protected final Map<N, BitSet> nodeToFlowSet;

    protected final Map<N, Integer> nodeToIndex;

    protected final Map<Integer, N> indexToNode;

    protected int lastIndex = 0;

    public MHGDominatorFinder(DirectedGraph<N> graph) {
        this.graph = graph;
        this.heads = new HashSet<>(graph.getHeads());
        int size = graph.size() * 2 + 1;
        this.nodeToFlowSet = new HashMap<>(size, 0.7f);
        this.nodeToIndex = new HashMap<>(size, 0.7f);
        this.indexToNode = new HashMap<>(size, 0.7f);
        doAnalysis();
    }

    protected void doAnalysis() {
        final DirectedGraph<N> graph = this.graph;

        // build full set
        BitSet fullSet = new BitSet(graph.size());
        fullSet.flip(0, graph.size());// set all to true

        // set up domain for intersection: head nodes are only dominated by themselves,
        // other nodes are dominated by everything else
        for (N o : graph) {
            if (heads.contains(o)) {
                BitSet self = new BitSet();
                self.set(indexOf(o));
                nodeToFlowSet.put(o, self);
            } else {
                nodeToFlowSet.put(o, fullSet);
            }
        }

        boolean changed;
        do {
            changed = false;
            for (N o : graph) {
                if (heads.contains(o)) {
                    continue;
                }

                // initialize to the "neutral element" for the intersection
                // this clone() is fast on BitSets (opposed to on HashSets)
                BitSet predsIntersect = (BitSet) fullSet.clone();

                // intersect over all predecessors
                for (N next : graph.getPredsOf(o)) {
                    predsIntersect.and(getDominatorsBitSet(next));
                }

                BitSet oldSet = getDominatorsBitSet(o);
                // each node dominates itself
                predsIntersect.set(indexOf(o));
                if (!predsIntersect.equals(oldSet)) {
                    nodeToFlowSet.put(o, predsIntersect);
                    changed = true;
                }
            }
        } while (changed);
    }

    protected BitSet getDominatorsBitSet(N node) {
        BitSet bitSet = nodeToFlowSet.get(node);
        assert (bitSet != null) : "Node " + node + " is not in the graph!";
        return bitSet;
    }

    protected int indexOfAssert(N o) {
        Integer index = nodeToIndex.get(o);
        assert (index != null) : "Node " + o + " is not in the graph!";
        return index;
    }

    protected int indexOf(N o) {
        Integer index = nodeToIndex.get(o);
        if (index == null) {
            index = lastIndex;
            nodeToIndex.put(o, index);
            indexToNode.put(index, o);
            lastIndex++;
        }
        return index;
    }

    @Override
    public DirectedGraph<N> getGraph() {
        return graph;
    }

    @Override
    public List<N> getDominators(N node) {
        // reconstruct list of dominators from bitset
        List<N> result = new ArrayList<N>();
        BitSet bitSet = getDominatorsBitSet(node);
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            result.add(indexToNode.get(i));
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }
        }
        return result;
    }

    @Override
    public N getImmediateDominator(N node) {
        // root node
        if (heads.contains(node)) {
            return null;
        }

        BitSet doms = (BitSet) getDominatorsBitSet(node).clone();
        doms.clear(indexOfAssert(node));

        for (int i = doms.nextSetBit(0); i >= 0; i = doms.nextSetBit(i + 1)) {
            N dominator = indexToNode.get(i);
            if (isDominatedByAll(dominator, doms)) {
                if (dominator != null) {
                    return dominator;
                }
            }
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }
        }
        return null;
    }

    private boolean isDominatedByAll(N node, BitSet doms) {
        BitSet s1 = getDominatorsBitSet(node);
        for (int i = doms.nextSetBit(0); i >= 0; i = doms.nextSetBit(i + 1)) {
            if (!s1.get(i)) {
                return false;
            }
            if (i == Integer.MAX_VALUE) {
                break; // or (i+1) would overflow
            }
        }
        return true;
    }

    @Override
    public boolean isDominatedBy(N node, N dominator) {
        return getDominatorsBitSet(node).get(indexOfAssert(dominator));
    }

    @Override
    public boolean isDominatedByAll(N node, Collection<N> dominators) {
        BitSet s1 = getDominatorsBitSet(node);
        for (N n : dominators) {
            if (!s1.get(indexOfAssert(n))) {
                return false;
            }
        }
        return true;
    }
}
