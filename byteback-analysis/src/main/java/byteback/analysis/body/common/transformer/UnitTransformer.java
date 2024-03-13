package byteback.analysis.body.common.transformer;

import byteback.analysis.body.common.syntax.MutableUnitBox;
import byteback.analysis.body.common.Body;
import byteback.analysis.body.jimple.syntax.Unit;
import byteback.analysis.body.common.syntax.UnitBox;

import java.util.Iterator;

/**
 * Body transformer that applies a transformation to each unit.
 *
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
