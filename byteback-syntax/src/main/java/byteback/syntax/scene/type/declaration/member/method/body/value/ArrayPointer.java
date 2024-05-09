package byteback.syntax.scene.type.declaration.member.method.body.value;

import java.util.Collections;
import java.util.List;

import byteback.syntax.scene.type.PointerType;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Jimple;
import soot.jimple.internal.ImmediateBox;

public class ArrayPointer implements Pointer, DefaultCaseValue {

	private final ImmediateBox indexBox;

	private final PointerType type;

	public ArrayPointer(final Value index, final Type type) {
		this.indexBox = new ImmediateBox(index);
		this.type = new PointerType(type);
	}

	public Value getIndex() {
		return indexBox.getValue();
	}

	public void setIndex(final Value value) {
		indexBox.setValue(value);
	}

	@Override
	public PointerType getType() {
		return type;
	}

	@Override
	public List<ValueBox> getUseBoxes() {
		return Collections.emptyList();
	}

	@Override
	public ArrayPointer clone() {
		return new ArrayPointer(Jimple.cloneIfNecessary(indexBox.getValue()), type.getPointedType());
	}

	@Override
	public int equivHashCode() {
		return 379541 * indexBox.getValue().equivHashCode();
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof final ArrayPointer arrayPointer
				&& getIndex().equals(arrayPointer.getIndex());
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("[");
		getIndex().toString(printer);
		printer.literal("]");
	}

}
