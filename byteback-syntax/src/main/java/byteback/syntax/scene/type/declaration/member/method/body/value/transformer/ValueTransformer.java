package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.UnitTransformer;
import soot.*;

import java.util.List;

/**
 * Body transformer that applies a transformation to each value *used and
 * defined* in the Body.
 *
 * @author paganma
 */
public abstract class ValueTransformer extends UnitTransformer {

	public abstract void transformValue(final ValueBox valueBox);

	public List<ValueBox> extractValueBoxes(final Unit unit) {
		return unit.getUseBoxes();
	}

	@Override
	public void transformUnit(final UnitBox unitBox) {
		final Unit unit = unitBox.getUnit();

		for (final ValueBox valueBox : extractValueBoxes(unit)) {
			transformValue(valueBox);
		}
	}

}
