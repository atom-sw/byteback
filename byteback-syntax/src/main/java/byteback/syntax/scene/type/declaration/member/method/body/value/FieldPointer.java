package byteback.syntax.scene.type.declaration.member.method.body.value;

import java.util.Collections;
import java.util.List;

import byteback.syntax.scene.type.PointerType;
import soot.SootField;
import soot.SootFieldRef;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.FieldRef;

public class FieldPointer implements FieldRef, Pointer, DefaultCaseValue {

	private SootFieldRef fieldRef;

	private final PointerType type;

	public FieldPointer(final SootFieldRef fieldRef) {
		this.fieldRef = fieldRef;
		this.type = new PointerType(fieldRef.type());
	}

	@Override
	public SootFieldRef getFieldRef() {
		return fieldRef;
	}

	@Override
	public void setFieldRef(final SootFieldRef fieldRef) {
		this.fieldRef = fieldRef;
	}

	@Override
	public SootField getField() {
		return fieldRef.resolve();
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
	public FieldPointer clone() {
		return new FieldPointer(fieldRef);
	}

	@Override
	public int equivHashCode() {
		return 94447 * fieldRef.hashCode();
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof final FieldPointer fieldPointer
				&& fieldRef.equals(fieldPointer.fieldRef);
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.fieldRef(fieldRef);
	}

}
