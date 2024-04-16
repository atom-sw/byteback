package byteback.syntax.type.declaration.method.body.value.transformer;

import byteback.syntax.type.declaration.method.body.context.BodyContext;
import byteback.syntax.type.declaration.method.body.unit.context.UnitContext;
import byteback.syntax.type.declaration.method.body.value.context.ValueContext;
import byteback.syntax.type.declaration.method.body.value.walker.ValueWalker;
import soot.Body;
import soot.UnitBox;
import soot.ValueBox;

/**
 * Body transformer that applies a transformation to each value *used and defined* in the Body.
 *
 * @author paganma
 */
public class ValueTransformer extends ValueWalker<BodyContext, UnitContext, ValueContext> {

	@Override
	public BodyContext makeBodyContext(final Body body) {
		return new BodyContext(body);
	}

	@Override
	public UnitContext makeUnitContext(final BodyContext bodyContext, final UnitBox unitBox) {
		return new UnitContext(bodyContext, unitBox);
	}

	@Override
	public ValueContext makeLocalValueContext(final UnitContext unitContext, final ValueBox valueBox) {
		return new ValueContext(unitContext, valueBox);
	}

	public void walkValue(final ValueContext valueContext) {
		transformValue(valueContext);
	}

	public void transformValue(final ValueContext valueContext) {}

}
