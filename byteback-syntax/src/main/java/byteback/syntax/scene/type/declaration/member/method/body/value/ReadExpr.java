package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Type;
import soot.Value;
import soot.jimple.Jimple;

public class ReadExpr extends HeapExpr {

	public ReadExpr(final Value heap, final Value base, final Value field) {
		super(heap, base, field);
	}

	@Override
	public Type getType() {
		return getField().getType();
	}

	@Override
	public int equivHashCode() {
		return 129671 * getHeap().equivHashCode() * getBase().equivHashCode();
	}

	@Override
	public HeapExpr clone() {
		return new ReadExpr(
				Jimple.cloneIfNecessary(getHeap()),
				Jimple.cloneIfNecessary(getBase()),
				Jimple.cloneIfNecessary(getField()));
	}

}
