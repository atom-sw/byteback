package byteback.analysis.body.jimple.transformer;


import byteback.analysis.body.common.syntax.Body;
import byteback.analysis.body.common.syntax.graph.MHGDominatorFinder;
import byteback.analysis.body.common.syntax.graph.UnitGraph;
import byteback.analysis.body.common.syntax.stmt.Stmt;
import byteback.analysis.body.common.syntax.stmt.Unit;
import byteback.analysis.body.common.transformer.BodyTransformer;
import byteback.analysis.body.common.transformer.Loop;

import java.util.*;

public class LoopFinder extends BodyTransformer {

    private Set<Loop> loops;

    public LoopFinder() {
        loops = null;
    }


    protected void internalTransform(final Body body) {
        getLoops(body);
    }

    public Set<Loop> getLoops(final Body body) {
        if (loops != null) {
            return loops;
        }

        return getLoops(ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body));
    }

    public Set<Loop> getLoops(UnitGraph g) {
        if (loops != null) {
            return loops;
        }

        MHGDominatorFinder<Unit> a = new MHGDominatorFinder<Unit>(g);
        Map<Stmt, List<Stmt>> loops = new HashMap<>();

        for (Unit u : g.getBody().getUnits()) {
            List<Unit> succs = g.getSuccsOf(u);
            List<Unit> dominaters = a.getDominators(u);
            List<Stmt> headers = new ArrayList<>();

            for (Unit succ : succs) {
                if (dominaters.contains(succ)) {
                    // header succeeds and dominates s, we have a loop
                    headers.add((Stmt) succ);
                }
            }

            for (Unit header : headers) {
                List<Stmt> loopBody = getLoopBodyFor(header, u, g);
                if (loops.containsKey(header)) {
                    // merge bodies
                    List<Stmt> lb1 = loops.get(header);
                    loops.put((Stmt) header, union(lb1, loopBody));
                } else {
                    loops.put((Stmt) header, loopBody);
                }
            }
        }

        Set<Loop> ret = new HashSet<Loop>();
        for (Map.Entry<Stmt, List<Stmt>> entry : loops.entrySet()) {
            ret.add(new Loop(entry.getKey(), entry.getValue(), g));
        }

        this.loops = ret;
        return ret;
    }

    private List<Stmt> getLoopBodyFor(Unit header, Unit node, UnitGraph g) {
        List<Stmt> loopBody = new ArrayList<Stmt>();
        Deque<Unit> stack = new ArrayDeque<Unit>();

        loopBody.add((Stmt) header);
        stack.push(node);

        while (!stack.isEmpty()) {
            Stmt next = (Stmt) stack.pop();
            if (!loopBody.contains(next)) {
                // add next to loop body
                loopBody.add(0, next);
                // put all preds of next on stack
                for (Unit u : g.getPredsOf(next)) {
                    stack.push(u);
                }
            }
        }

        assert (node == header && loopBody.size() == 1) || loopBody.get(loopBody.size() - 2) == node;
        assert loopBody.get(loopBody.size() - 1) == header;

        return loopBody;
    }

    private List<Stmt> union(List<Stmt> l1, List<Stmt> l2) {
        for (Stmt next : l2) {
            if (!l1.contains(next)) {
                l1.add(next);
            }
        }
        return l1;
    }
}