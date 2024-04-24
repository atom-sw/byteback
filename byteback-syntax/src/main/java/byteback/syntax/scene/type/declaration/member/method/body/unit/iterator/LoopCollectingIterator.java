package byteback.syntax.scene.type.declaration.member.method.body.unit.iterator;

import soot.Unit;
import soot.jimple.toolkits.annotation.logic.Loop;
import soot.util.Chain;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Wraps a unit iterator to provide information on which traps are active at the current unit.
 *
 * @author paganma
 */
public class LoopCollectingIterator implements Iterator<Unit> {

    private final Iterator<Unit> unitIterator;

    private final ArrayDeque<Loop> activeLoops;

    private final HashMap<Unit, Loop> headToLoop;

    private final HashMap<Unit, Loop> tailToLoop;

    /**
     * Constructs a new trap-collecting unit iterator.
     *
     * @param units The underlying units.
     * @param loops The loops that this iterator must be made aware of.
     */
    public LoopCollectingIterator(final Chain<Unit> units, final Iterable<Loop> loops) {
        this.unitIterator = units.snapshotIterator();
        this.activeLoops = new ArrayDeque<>();
        this.headToLoop = new HashMap<>();
        this.tailToLoop = new HashMap<>();

        for (final Loop loop : loops) {
            headToLoop.put(loop.getHead(), loop);
            tailToLoop.put(loop.getBackJumpStmt(), loop);
        }
    }

    /**
     * Getter for the active loops at the current point in the iteration.
     *
     * @return The active loops at the current position in the iteration.
     */
    public ArrayDeque<Loop> getActiveLoops() {
        return activeLoops;
    }

    @Override
    public boolean hasNext() {
        return unitIterator.hasNext();
    }

    @Override
    public Unit next() {
        final Unit nextUnit = unitIterator.next();
        final Loop startedLoop = headToLoop.get(nextUnit);

        if (startedLoop != null) {
            activeLoops.push(startedLoop);
        }

        final Loop endedLoop = tailToLoop.get(nextUnit);

        if (endedLoop != null) {
            activeLoops.remove(endedLoop);
        }

        return nextUnit;
    }

}
