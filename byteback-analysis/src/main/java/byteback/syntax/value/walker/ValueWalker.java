package byteback.syntax.value.walker;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import byteback.syntax.type.declaration.method.body.unit.context.UnitContext;
import byteback.syntax.type.declaration.method.body.unit.walker.UnitWalker;
import byteback.syntax.value.context.ValueContext;
import soot.Unit;
import soot.UnitBox;
import soot.ValueBox;

import java.util.List;

/**
 * Body transformer that applies a transformation to each value *used and defined* in the Body.
 *
 * @author paganma
 */
public abstract class ValueWalker<B extends BodyContext, U extends UnitContext, V extends ValueContext>
		extends UnitWalker<B, U> {

	public abstract V makeLocalValueContext(final U unitContext, final ValueBox valueBox);

	public abstract void walkValue(final V valueContext);

	public List<ValueBox> extractValueBoxes(final Unit unit) {
		return unit.getUseAndDefBoxes();
	}

	@Override
	public void walkUnit(final U localUnitContext) {
		final UnitBox unitBox = localUnitContext.getUnitBox();
		final Unit unit = unitBox.getUnit();

		for (final ValueBox valueBox : extractValueBoxes(unit)) {
			final V valueContext = makeLocalValueContext(localUnitContext, valueBox);
			walkValue(valueContext);
		}
	}

}
