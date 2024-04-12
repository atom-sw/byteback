package byteback.syntax.unit.transformer;

import byteback.syntax.unit.box.MutableUnitBox;
import byteback.syntax.member.method.body.transformer.BodyTransformer;
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
            transformUnit(body, new MutableUnitBox(unit, body));
        }
    }

    /**
     * Defines a transformation on a particular unit.
     * @param body The body surrounding the unit.
     * @param unitBox The box that contains the unit to be transformed. The result of the transformation must be placed
     *                in this same box.
     */
    public abstract void transformUnit(Body body, UnitBox unitBox);

}
