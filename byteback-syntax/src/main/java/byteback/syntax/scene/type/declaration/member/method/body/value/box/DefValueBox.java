package byteback.syntax.scene.type.declaration.member.method.body.value.box;

import soot.AbstractValueBox;
import soot.Immediate;
import soot.Value;
import soot.jimple.ConcreteRef;
import soot.jimple.Expr;
import soot.jimple.IdentityRef;

public class DefValueBox extends AbstractValueBox {

	public DefValueBox(final Value value) {
		setValue(value);
	}

	@Override
	public boolean canContainValue(final Value value) {
		return value instanceof Immediate
				|| value instanceof IdentityRef
				|| value instanceof ConcreteRef
				|| value instanceof Expr;
	}

}
