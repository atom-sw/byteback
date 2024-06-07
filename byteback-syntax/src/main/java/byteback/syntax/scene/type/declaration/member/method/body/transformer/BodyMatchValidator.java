package byteback.syntax.scene.type.declaration.member.method.body.transformer;

import byteback.syntax.scene.type.declaration.member.method.body.unit.box.ImmutableUnitBox;
import byteback.syntax.scene.type.declaration.member.method.body.value.box.ImmutableValueBox;
import byteback.syntax.transformer.TransformationException;
import soot.*;

public abstract class BodyMatchValidator extends BodyTransformer {

	public abstract boolean admitsDef(final ValueBox value);

	public abstract boolean admitsUse(final ValueBox value);

	public abstract boolean admitsUnit(final UnitBox unit);

	@Override
	public void transformBody(final SootMethod sootMethod, final Body body) {
		for (final Unit unit : body.getUnits()) {
			final var unitBox = new ImmutableUnitBox(unit);

			if (!admitsUnit(unitBox)) {
				throw new TransformationException("Invalid statement: " + unit + ".", unit);
			}

			for (final ValueBox useBox : unit.getUseBoxes()) {
				final Value value = useBox.getValue();
				final var immutableUseBox = new ImmutableValueBox(value);
			}

			for (final ValueBox defBox : unit.getDefBoxes()) {
				final Value value = defBox.getValue();
				final var immutableUseBox = new ImmutableValueBox(value);
			}
		}
	}

}
