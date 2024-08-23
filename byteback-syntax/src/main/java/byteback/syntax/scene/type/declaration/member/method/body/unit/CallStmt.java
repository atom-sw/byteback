package byteback.syntax.scene.type.declaration.member.method.body.unit;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import byteback.syntax.scene.type.declaration.member.method.body.value.CallExpr;
import byteback.syntax.scene.type.declaration.member.method.body.value.box.ValueBox;
import soot.UnitPrinter;
import soot.Value;
import soot.jimple.internal.InvokeExprBox;
import soot.jimple.internal.JInvokeStmt;

public class CallStmt extends JInvokeStmt {

	final List<ValueBox> targetBoxes;

	public CallStmt(final Value[] targets, final CallExpr callExpr) {
		super(new InvokeExprBox(callExpr));
		this.targetBoxes = Arrays.stream(targets)
				.map(ValueBox::new)
				.toList();
	}

	public List<Value> getTargets() {
		return targetBoxes.stream().map(ValueBox::getValue).toList();
	}

	@Override
	public List<soot.ValueBox> getDefBoxes() {
		return Collections.unmodifiableList(targetBoxes);
	}

	public void toString(final UnitPrinter printer) {
		printer.literal("call ");

		final Iterator<Value> valueIterator = getTargets().iterator();

		while (valueIterator.hasNext()) {
			final Value value = valueIterator.next();
			value.toString(printer);

			if (valueIterator.hasNext()) {
				printer.literal(", ");
			}
		}

		printer.literal(" := ");

		getInvokeExpr().toString(printer);
	}

}
