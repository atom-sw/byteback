package byteback.syntax.scene.type.declaration.member.method.body.value;

import byteback.syntax.scene.type.HeapType;
import soot.Type;
import soot.Value;
import soot.jimple.Jimple;

public class UpdateExpr extends HeapExpr {

	public UpdateExpr(final Value heap, final Value base, final Value field) {
		super(heap, base, field);
	}

	@Override
	public Type getType() {
		return HeapType.v();
	}

	@Override
	public int equivHashCode() {
		return 843487 * getHeap().equivHashCode() * getBase().equivHashCode();
	}

	@Override
	public UpdateExpr clone() {
		return new UpdateExpr(
				Jimple.cloneIfNecessary(getHeap()),
				Jimple.cloneIfNecessary(getBase()),
				Jimple.cloneIfNecessary(getPointer()));
	}

}
