package byteback.analysis.body.common.syntax;

import byteback.analysis.body.common.Traps;
import soot.Trap;
import soot.Unit;
import soot.util.Chain;

import java.util.Comparator;

public class TrapRangeComparator implements Comparator<Trap> {

    final Chain<Unit> units;

    public TrapRangeComparator(final Chain<Unit> units) {
        this.units = units;
    }

    @Override
    public int compare(final Trap trap1, final Trap trap2) {
        final Chain<Unit> units1 = Traps.v().getUnitsCovered(units, trap1);
        final Chain<Unit> units2 = Traps.v().getUnitsCovered(units, trap2);

        if (units1.equals(units2)) {
            return 0;
        } else if (units1.containsAll(units2)) {
            return 1;
        } else if (units2.containsAll(units1)) {
            return -1;
        }

        return 1;
    }
}
