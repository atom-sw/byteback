package byteback.syntax.scene.type.declaration.member.method.body.unit.iterator;

import soot.Trap;
import soot.Unit;
import soot.util.Chain;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Wraps a unit iterator to provide information on which traps are active at the current unit.
 *
 * @author paganma
 */
public class TrapCollectingIterator implements Iterator<Unit> {

    private final Iterator<Unit> unitIterator;

    private final ArrayDeque<Trap> activeTraps;

    private final HashMap<Unit, ArrayDeque<Trap>> startToTraps;

    private final HashMap<Unit, ArrayDeque<Trap>> endToTraps;

    /**
     * Constructs a new trap-collecting unit iterator.
     *
     * @param units The underlying units.
     * @param traps The traps that this iterator must be made aware of.
     */
    public TrapCollectingIterator(final Chain<Unit> units, final Iterable<Trap> traps) {
        this.unitIterator = units.snapshotIterator();
        this.activeTraps = new ArrayDeque<>();
        this.startToTraps = new HashMap<>();
        this.endToTraps = new HashMap<>();

        for (final Trap trap : traps) {
            final ArrayDeque<Trap> startedTraps =
                    startToTraps.computeIfAbsent(trap.getBeginUnit(), ($) -> new ArrayDeque<>());
            startedTraps.add(trap);
            final ArrayDeque<Trap> endedTraps =
                    endToTraps.computeIfAbsent(trap.getEndUnit(), ($) -> new ArrayDeque<>());
            endedTraps.add(trap);
        }
    }

    /**
     * Getter for the active traps at the current point in the iteration.
     *
     * @return The active traps at the current position in the iteration.
     */
    public ArrayDeque<Trap> getActiveTraps() {
        return activeTraps;
    }

    @Override
    public boolean hasNext() {
        return unitIterator.hasNext();
    }

    @Override
    public Unit next() {
        final Unit nextUnit = unitIterator.next();
        final Deque<Trap> startedTraps = startToTraps.get(nextUnit);

        if (startedTraps != null) {
            activeTraps.addAll(startedTraps);
        }

        final Deque<Trap> endedTraps = endToTraps.get(nextUnit);

        if (endedTraps != null) {
            activeTraps.removeAll(endedTraps);
        }

        return nextUnit;
    }

}
