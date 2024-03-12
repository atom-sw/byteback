package soot.jimple.toolkits.thread.mhp;

import heros.solver.Pair;
import soot.toolkits.graph.DirectedGraph;
import soot.util.FastStack;

import java.util.*;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class SCC<T> {

    private Set<T> gray;
    private final LinkedList<T> finishedOrder;
    private final List<List<T>> sccList;

    public SCC(Iterator<T> it, DirectedGraph<T> g) {

        gray = new HashSet<T>();
        finishedOrder = new LinkedList<T>();
        sccList = new ArrayList<List<T>>();

        // Visit each node
        {

            while (it.hasNext()) {
                T s = it.next();
                if (!gray.contains(s)) {

                    visitNode(g, s);
                }
            }

        }

        // Re-color all nodes white
        gray = new HashSet<T>();

        // visit nodes via tranpose edges according decreasing order of finish time of nodes

        {

            Iterator<T> revNodeIt = finishedOrder.iterator();
            while (revNodeIt.hasNext()) {
                T s = revNodeIt.next();
                if (!gray.contains(s)) {

                    List<T> scc = new ArrayList<T>();

                    visitRevNode(g, s, scc);
                    sccList.add(scc);
                }

            }
        }
    }

    private void visitNode(DirectedGraph<T> g, T s) {
        gray.add(s);
        FastStack<Pair<T, Iterator<T>>> stack = new FastStack<>();
        stack.push(new Pair<>(s, g.getSuccsOf(s).iterator()));
        next:
        while (!stack.isEmpty()) {

            Pair<T, Iterator<T>> p = stack.peek();
            Iterator<T> it = p.getO2();
            while (it.hasNext()) {
                T succ = it.next();
                if (!gray.contains(succ)) {
                    gray.add(succ);
                    stack.push(new Pair<T, Iterator<T>>(succ, g.getSuccsOf(succ).iterator()));
                    continue next;
                }
            }
            stack.pop();
            finishedOrder.addFirst(p.getO1());
        }
    }

    private void visitRevNode(DirectedGraph<T> g, T s, List<T> scc) {

        scc.add(s);
        gray.add(s);

        FastStack<Iterator<T>> stack = new FastStack<>();
        stack.push(g.getPredsOf(s).iterator());

        next:
        while (!stack.isEmpty()) {

            Iterator<T> predsIt = stack.peek();
            while (predsIt.hasNext()) {
                T pred = predsIt.next();
                if (!gray.contains(pred)) {
                    scc.add(pred);
                    gray.add(pred);
                    stack.push(g.getPredsOf(pred).iterator());
                    continue next;
                }
            }
            stack.pop();
        }
    }

    public List<List<T>> getSccList() {
        return sccList;
    }

    public LinkedList<T> getFinishedOrder() {
        return finishedOrder;
    }
}