package byteback.analysis.body.common.transformer;

import byteback.analysis.body.common.syntax.MutableUnitBox;
import soot.Body;
import soot.Unit;
import soot.UnitBox;

import java.util.Iterator;

/**
 * Body transformer that applies a transformation to each unit.
 * @author paganma
 */
public abstract class UnitTransformer extends BodyTransformer {

    @Override
    public void transformBody(final Body body) {
        final Iterator<Unit> iterator = body.getUnits().snapshotIterator();

        while (iterator.hasNext()) {
            final Unit unit = iterator.next();
            transformUnit(new MutableUnitBox(unit, body));
        }
    }

    public abstract void transformUnit(UnitBox unitBox);

}
