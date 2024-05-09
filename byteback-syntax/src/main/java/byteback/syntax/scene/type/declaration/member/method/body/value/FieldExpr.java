package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.SootFieldRef;
import soot.Type;
import soot.Value;
import soot.jimple.Jimple;

public class FieldExpr {

	public ReadExpr(final Value heap, final Value base, final SootFieldRef fieldRef) {
		super(heap, base, fieldRef);
	}

}
