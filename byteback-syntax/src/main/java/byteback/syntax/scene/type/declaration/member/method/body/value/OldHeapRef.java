package byteback.syntax.scene.type.declaration.member.method.body.value;

import java.util.Collections;
import java.util.List;

import byteback.syntax.scene.type.HeapType;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.ConcreteRef;
import soot.jimple.IdentityRef;

public class OldHeapRef implements IdentityRef, ConcreteRef, DefaultCaseValue {

	public OldHeapRef() {
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof final ReturnRef returnRef
				&& returnRef.getType().equals(getType());
	}

	@Override
	public int equivHashCode() {
		return 31 * getType().hashCode();
	}

	@Override
	public List<ValueBox> getUseBoxes() {
		return Collections.emptyList();
	}

	@Override
	public Type getType() {
		return HeapType.v();
	}

	@Override
	public Object clone() {
		return new OldHeapRef();
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("@heap'");
	}

}
