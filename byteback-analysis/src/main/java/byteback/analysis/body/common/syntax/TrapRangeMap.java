package byteback.analysis.body.common.syntax;

import soot.Body;
import soot.Trap;
import soot.Unit;

import java.util.*;

public class TrapRangeMap extends TreeMap<Unit, Queue<TrapDelimiter>> {


    public TrapRangeMap(final Body body) {
        super(new UnitRangeComparator(body.getUnits()));

        for (final Trap trap : body.getTraps()) {
            final Queue<TrapDelimiter> trapStarts = computeIfAbsent(trap.getBeginUnit(), ($) -> new ArrayDeque<>());
            trapStarts.add(new TrapDelimiter(trap, TrapDelimiter.Type.STARTER));
            final Queue<TrapDelimiter> trapEnds = computeIfAbsent(trap.getEndUnit(), ($) -> new ArrayDeque<>());
            trapEnds.add(new TrapDelimiter(trap, TrapDelimiter.Type.ENDER));
        }
    }

    public LinkedHashSet<Trap> trapsAt(final Unit unit) {
        final SortedMap<Unit, Queue<TrapDelimiter>> headMap = headMap(unit);
        final SortedMap<Unit, Queue<TrapDelimiter>> tailMap = tailMap(unit);
        final var startedTraps = new LinkedHashSet<Trap>();
        final var endedTraps = new LinkedHashSet<Trap>();


        for (final Map.Entry<Unit, Queue<TrapDelimiter>> startDelimiters : headMap.entrySet()) {
            for (final TrapDelimiter startDelimiter : startDelimiters.getValue()) {
                if (startDelimiter.getType() == TrapDelimiter.Type.STARTER) {
                    startedTraps.add(startDelimiter.getTrap());
                }
            }
        }

        for (final Map.Entry<Unit, Queue<TrapDelimiter>> startDelimiters : tailMap.entrySet()) {
            for (final TrapDelimiter startDelimiter : startDelimiters.getValue()) {
                if (startDelimiter.getType() == TrapDelimiter.Type.ENDER) {
                    endedTraps.add(startDelimiter.getTrap());
                }
            }
        }

        startedTraps.retainAll(endedTraps);

        return startedTraps;
    }

}
