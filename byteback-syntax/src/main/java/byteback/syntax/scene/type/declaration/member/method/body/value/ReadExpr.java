package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Type;
import soot.Value;
import soot.jimple.Jimple;

public class ReadExpr extends DerefExpr {

	public ReadExpr(final Value heap, final Value base, final Value pointer) {
		super(heap, base, pointer);
	}

	@Override
	public Type getType() {
		return getPointer().getType().getPointedType();
	}

	@Override
	public int equivHashCode() {
		return 129671 * getHeap().equivHashCode() * getBase().equivHashCode();
	}

	@Override
	public DerefExpr clone() {
		return new ReadExpr(
				Jimple.cloneIfNecessary(getHeap()),
				Jimple.cloneIfNecessary(getBase()),
				Jimple.cloneIfNecessary(getPointer()));
	}

}
