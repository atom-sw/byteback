package byteback.analysis.body.common.transformer;

import byteback.analysis.body.common.Body;
import byteback.analysis.body.common.syntax.UnitBox;
import byteback.analysis.body.common.syntax.ValueBox;
import byteback.analysis.body.jimple.syntax.Unit;

/**
 * Body transformer that applies a transformation to each value.
 *
 * @author paganma
 */
public abstract class ValueTransformer extends UnitTransformer {

    public abstract void transformValue(ValueBox valueBox);

    @Override
    public void transformUnit(final UnitBox unitBox) {
        final Unit unit = unitBox.getUnit();

        for (final ValueBox useBox : unit.getUseBoxes()) {
            transformValue(useBox);
        }
    }

    @Override
    public void transformBody(final Body body) {
        for (final ValueBox useBox : body.getUseBoxes()) {
            transformValue(useBox);
        }
    }

}
