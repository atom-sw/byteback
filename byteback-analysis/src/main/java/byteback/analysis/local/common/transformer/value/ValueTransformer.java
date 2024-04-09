package byteback.analysis.local.common.transformer.value;

import byteback.analysis.local.common.syntax.unit.MutableUnitBox;
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

		for (final Unit unit : body.getUnits()) {
			transformUnit(body, new MutableUnitBox(unit, body));
		}
	}

	@Override
	public void transformUnit(final Body body, final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();

		for (final ValueBox valueBox : unit.getUseAndDefBoxes()) {
			transformValue(body, unitBox, valueBox);
		}
	}

	public abstract void transformValue(final Body body, final UnitBox unitBox, final ValueBox valueBox);

}
