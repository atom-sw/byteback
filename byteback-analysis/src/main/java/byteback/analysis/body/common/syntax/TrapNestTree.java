package byteback.analysis.body.common.syntax;

import soot.Body;
import soot.Trap;
import soot.Unit;

import java.util.*;

/**
 * A tree set that can be iterated to retrieve .
 * @author paganma
 */
public class TrapNestTree extends TreeSet<Trap> {

    public TrapNestTree(final Body body) {
        super(new TrapRangeComparator(body.getUnits()));
        addAll(body.getTraps());
    }

}
