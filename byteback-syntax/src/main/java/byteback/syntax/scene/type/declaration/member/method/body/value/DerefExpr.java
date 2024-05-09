package byteback.syntax.scene.type.declaration.member.method.body.value;

import java.util.ArrayList;
import java.util.List;

import byteback.syntax.scene.type.declaration.member.method.body.value.box.PointerBox;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Expr;
import soot.jimple.internal.ImmediateBox;

public abstract class DerefExpr implements Expr, DefaultCaseValue {

	private final ImmediateBox heapBox;

	private final ImmediateBox baseBox;

	private final PointerBox pointerBox;

	public DerefExpr(final ImmediateBox heapBox, final ImmediateBox baseBox, final PointerBox pointerBox) {
		this.heapBox = heapBox;
		this.baseBox = baseBox;
		this.pointerBox = pointerBox;
	}

	public DerefExpr(final Value heap, final Value base, final Value field) {
		this(new ImmediateBox(heap), new ImmediateBox(base), new PointerBox(field));
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

	public ValueBox getPointerBox() {
		return pointerBox;
	}

	public Pointer getPointer() {
		return pointerBox.getValue();
	}

	@Override
	public Object clone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void toString(final UnitPrinter printer) {
		getHeap().toString(printer);
		printer.literal("[");
		getBase().toString(printer);
		printer.literal("]");
		printer.literal("[");
		getPointer().toString(printer);
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
