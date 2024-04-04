package byteback.analysis.local.common.transformer.value;

import byteback.analysis.local.common.transformer.unit.UnitTransformer;
import soot.Body;
import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;

/**
 * Body transformer that applies a transformation to each value *used and defined* in the Body.
 *
 * @author paganma
 */
public abstract class ValueTransformer extends UnitTransformer {

	@Override
	public void transformBody(final Body body) {
		for (final ValueBox useBox : body.getUseBoxes()) {
			transformValue(useBox);
		}
	}

	@Override
	public void transformUnit(final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();

		for (final ValueBox useBox : unit.getUseAndDefBoxes()) {
			transformValue(useBox);
		}
	}

	/**
	 * Defines a transformation on a particular value.
	 * @param valueBox The box that contains the value to be transformed. The result of the transformation must be
	 *                 placed in this same box.
	 */
	public abstract void transformValue(ValueBox valueBox);

}
