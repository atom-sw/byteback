package byteback.analysis.body.common.transformer;

import soot.Body;
import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;

import java.util.Map;

/**
 * Body transformer that applies a transformation to each value.
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
	public void internalTransform(final Body body, final String phaseName, final Map<String, String> options) {
		for (final ValueBox useBox : body.getUseBoxes()) {
			transformValue(useBox);
		}
	}

}
