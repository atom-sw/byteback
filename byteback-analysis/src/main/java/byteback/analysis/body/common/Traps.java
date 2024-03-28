package byteback.analysis.body.common;

import byteback.common.function.Lazy;
import soot.Trap;
import soot.Unit;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.*;

/**
 * Utility function to work with Soot traps.
 *
 * @author paganma
 */
public class Traps {

    private static final Lazy<Traps> instance = Lazy.from(Traps::new);

    private Traps() {
    }

    public static Traps v() {
        return instance.get();
    }

    public Chain<Unit> getUnitsCovered(final Chain<Unit> units, final Trap trap) {
        final var trapUnits = new HashChain<Unit>();
        final Iterator<Unit> trapIterator = units.iterator(trap.getBeginUnit(), trap.getEndUnit());
        trapIterator.forEachRemaining(trapUnits::add);

        return trapUnits;
    }

}
