package byteback.analysis.local.common.transformer.unit;

import byteback.analysis.local.common.syntax.unit.MutableUnitBox;
import byteback.analysis.local.common.transformer.body.BodyTransformer;
import soot.Body;
import soot.Unit;
import soot.UnitBox;

import java.util.Iterator;

/**
 * Body transformer that applies a transformation to each unit.
 *
 * @author paganma
 */
public abstract class UnitTransformer extends BodyTransformer {

    /**
     * Applies a transformation to each unit in the input body.
     * @param body The Body to be transformed.
     */
    @Override
    public void transformBody(final Body body) {
        final Iterator<Unit> iterator = body.getUnits().snapshotIterator();

        while (iterator.hasNext()) {
            final Unit unit = iterator.next();
            transformUnit(new MutableUnitBox(unit, body));
        }
    }

    /**
     * Defines a transformation on a particular unit.
     * @param unitBox The box that contains the unit to be transformed. The result of the transformation must be placed
     *                in this same box.
     */
    public abstract void transformUnit(UnitBox unitBox);

}
