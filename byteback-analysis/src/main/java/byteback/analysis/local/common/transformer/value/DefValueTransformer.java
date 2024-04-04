package byteback.analysis.local.common.transformer.value;

import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;

/**
 * Body transformer that applies a transformation to each value *used* in the Body.
 *
 * @author paganma
 */
public abstract class DefValueTransformer extends ValueTransformer {

    @Override
    public void transformUnit(final UnitBox unitBox) {
        final Unit unit = unitBox.getUnit();

        for (final ValueBox useBox : unit.getDefBoxes()) {
            transformValue(useBox);
        }
    }

}
