package byteback.analysis.body.common.syntax;

import soot.Unit;
import soot.util.Chain;

import java.util.Comparator;

/**
 * Compares two units based on their order in a unit chain.
 * @author paganma
 */
public class UnitRangeComparator implements Comparator<Unit> {

    final Chain<Unit> units;

    public UnitRangeComparator(final Chain<Unit> units) {
        this.units = units;
    }

    @Override
    public int compare(final Unit unit1, final Unit unit2) {
        if (unit1.equals(unit2)) {
            return 0;
        } else if (units.follows(unit1, unit2)) {
            return 1;
        } else {
            return -1;
        }
    }

}
