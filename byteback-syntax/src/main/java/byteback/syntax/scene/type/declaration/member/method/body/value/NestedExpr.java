package byteback.syntax.scene.type.declaration.member.method.body.value;

import soot.*;
import soot.jimple.Jimple;

import java.util.ArrayList;
import java.util.List;

import byteback.syntax.scene.type.declaration.member.method.body.value.box.ValueBox;

public class NestedExpr implements Immediate, DefaultCaseValue {

	private final ValueBox valueBox;

	public NestedExpr(final ValueBox valueBox) {
		this.valueBox = valueBox;
	}

	public NestedExpr(final Value value) {
		this(new ValueBox(value));
	}

	public final Value getValue() {
		return valueBox.getValue();
	}

	@Override
	public List<soot.ValueBox> getUseBoxes() {
		final var useBoxes = new ArrayList<soot.ValueBox>();
		useBoxes.add(valueBox);
		final Value value = valueBox.getValue();
		useBoxes.addAll(value.getUseBoxes());

		return useBoxes;
	}

	@Override
	public Type getType() {
		final Value value = valueBox.getValue();

		return value.getType();
	}

	@Override
	public Object clone() {
		return new NestedExpr(Jimple.cloneIfNecessary(getValue()));
	}

	@Override
	public boolean equivTo(final Object object) {
		final Value value = valueBox.getValue();

		return value.equivTo(object);
	}

	@Override
	public int equivHashCode() {
		final Value value = valueBox.getValue();

		return value.equivHashCode();
	}

	@Override
	public void toString(final UnitPrinter printer) {
		printer.literal("<");
		getValue().toString(printer);
		printer.literal(">");
	}

}
