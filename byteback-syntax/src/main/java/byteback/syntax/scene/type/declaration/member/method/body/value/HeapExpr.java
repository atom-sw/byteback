package byteback.syntax.scene.type.declaration.member.method.body.value;

import java.util.ArrayList;
import java.util.List;

import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Expr;
import soot.jimple.internal.ImmediateBox;

public abstract class HeapExpr implements Expr, DefaultCaseValue {

	private final ImmediateBox heapBox;

	private final ImmediateBox baseBox;

	private final ImmediateBox fieldBox;

	public HeapExpr(final ImmediateBox heapBox, final ImmediateBox baseBox, final ImmediateBox fieldBox) {
		this.heapBox = heapBox;
		this.baseBox = baseBox;
		this.fieldBox = fieldBox;
	}

	public HeapExpr(final Value heap, final Value base, final Value field) {
		this(new ImmediateBox(heap), new ImmediateBox(base), new ImmediateBox(field));
	}

	public ValueBox getHeapBox() {
		return heapBox;
	}

	public Value getHeap() {
		return heapBox.getValue();
	}

	public ValueBox getBaseBox() {
		return baseBox;
	}

	public Value getBase() {
		return baseBox.getValue();
	}

	public ValueBox getFieldBox() {
		return fieldBox;
	}

	public Value getField() {
		return fieldBox.getValue();
	}

	@Override
	public Object clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void toString(final UnitPrinter printer) {
		getHeap().toString(printer);
		printer.literal("[");
		getBase().toString(printer);;
		printer.literal("]");
		printer.literal("[");
		printer.literal("]");
	}

	@Override
	public boolean equivTo(final Object object) {
		return object instanceof final ReadExpr readExpr
			&& readExpr.getBase().equivTo(getBase())
			&& readExpr.getHeap().equivTo(getHeap());
	}

	@Override
	public List<ValueBox> getUseBoxes() {
		final var useBoxes = new ArrayList<ValueBox>();
		useBoxes.add(getHeapBox());
		useBoxes.add(getBaseBox());

		return useBoxes;
	}

}
