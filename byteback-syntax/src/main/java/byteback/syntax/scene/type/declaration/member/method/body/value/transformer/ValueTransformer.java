package byteback.syntax.scene.type.declaration.member.method.body.value.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.UnitTransformer;
import byteback.syntax.scene.type.declaration.member.method.body.unit.transformer.context.UnitTransformationContext;
import byteback.syntax.scene.type.declaration.member.method.body.value.transformer.context.ValueTransformationContext;
import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;

import java.util.List;

/**
 * Body transformer that applies a transformation to each value *used and defined* in the Body.
 *
 * @author paganma
 */
public abstract class ValueTransformer extends UnitTransformer {

	public abstract void transformValue(final ValueTransformationContext valueContext);

	public List<ValueBox> extractValueBoxes(final Unit unit) {
		return unit.getUseAndDefBoxes();
	}

	@Override
	public void transformUnit(final UnitTransformationContext unitTransformationContext) {
		final UnitBox unitBox = unitTransformationContext.getUnitBox();
		final Unit unit = unitBox.getUnit();

		for (final ValueBox valueBox : extractValueBoxes(unit)) {
			final var valueContext = new ValueTransformationContext(unitTransformationContext, valueBox);
			transformValue(valueContext);
		}
	}

}
