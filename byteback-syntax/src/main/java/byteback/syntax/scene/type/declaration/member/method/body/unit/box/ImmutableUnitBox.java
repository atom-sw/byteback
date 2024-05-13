package byteback.syntax.scene.type.declaration.member.method.body.unit.box;

import soot.AbstractUnitBox;
import soot.Unit;

public class ImmutableUnitBox extends AbstractUnitBox {

	public ImmutableUnitBox(final Unit unit) {
		this.unit = unit;
	}

	@Override
	public boolean canContainUnit(final Unit unit) {
		return true;
	}

	@Override
	public void setUnit(final Unit unit) {
		throw new UnsupportedOperationException();
	}

}
