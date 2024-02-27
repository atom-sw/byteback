package byteback.analysis.body.common.transformer;

import byteback.analysis.body.vimp.SwappableUnitBox;
import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.UnitBox;

import java.util.Iterator;
import java.util.Map;

public abstract class UnitTransformer extends BodyTransformer {

    @Override
    public void internalTransform(final Body b, final String phaseName, final Map<String, String> options) {
        final Iterator<Unit> iterator = b.getUnits().snapshotIterator();

        while (iterator.hasNext()) {
            final Unit unit = iterator.next();
            transformUnit(new SwappableUnitBox(unit, b));
        }
    }

    abstract void transformUnit(UnitBox unitBox);

}
