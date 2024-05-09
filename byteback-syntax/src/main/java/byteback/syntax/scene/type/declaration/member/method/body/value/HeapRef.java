package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.ConcreteRef;

import java.util.Collections;
import java.util.List;

import byteback.syntax.scene.type.HeapType;

/**
 * Reference corresponding to the heap manipulated by a method.
 *
 * @author paganma
 */
public class HeapRef implements ConcreteRef, DefaultCaseValue {

	public HeapRef() {
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof final ReturnRef returnRef
				&& returnRef.getType().equals(getType());
	}

	@Override
	public int equivHashCode() {
		return 67 * getType().hashCode();
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
		return new HeapRef();
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("@heap");
	}

}
