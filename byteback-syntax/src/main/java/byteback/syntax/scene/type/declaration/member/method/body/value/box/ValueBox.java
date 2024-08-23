package byteback.syntax.scene.type.declaration.member.method.body.value.box;

import soot.AbstractValueBox;
import soot.Value;

public class ValueBox extends AbstractValueBox {

	public ValueBox(final Value value) {
		this.value = value;
	}

	public boolean canContainValue(final Value value) {
		return true;
	}

}
