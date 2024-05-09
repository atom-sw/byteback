package byteback.syntax.scene.type.declaration.member.method.body.value.box;

import byteback.syntax.scene.type.declaration.member.method.body.value.Pointer;
import soot.AbstractValueBox;
import soot.Value;

public class PointerBox extends AbstractValueBox {

	public PointerBox(final Value pointer) {
		setValue(pointer);
	}

	@Override
	public boolean canContainValue(final Value value) {
		return value instanceof Pointer;
	}

	@Override
	public void setValue(final Value value) {
		this.value = value;
	}

	@Override
	public Pointer getValue() {
		return (Pointer) super.getValue();
	}

}
