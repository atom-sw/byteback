package byteback.analysis.body.common.syntax;

import soot.Trap;
import soot.Unit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Wraps a unit iterator to provide information on which traps are active at the current unit.
 *
 * @author paganma
 */
public class TrapCollector implements Iterator<Unit> {


    private final Iterator<Unit> unitIterator;

    private final ArrayDeque<Trap> activeTraps;

    private final HashMap<Unit, ArrayDeque<Trap>> startToTraps;

    private final HashMap<Unit, ArrayDeque<Trap>> endToTraps;

    public TrapCollector(final Iterator<Unit> unitIterator, final Iterable<Trap> traps) {
        this.unitIterator = unitIterator;
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
