package byteback.syntax.scene.type.declaration.member.method.body.unit;

import java.util.ArrayList;
import java.util.List;

import byteback.syntax.scene.type.declaration.member.method.body.unit.printer.InlineUnitPrinter;
import byteback.syntax.Vimp;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.AbstractStmt;

/**
 * Base specification statement.
 *
 * @author paganma
 */
public abstract class SpecificationStmt extends AbstractStmt {

	private final ValueBox conditionBox;

	public SpecificationStmt(final Value condition) {
		this.conditionBox = Vimp.v().newConditionExprBox(condition);
	}

	public ValueBox getConditionBox() {
		return conditionBox;
	}

	public Value getCondition() {
		return conditionBox.getValue();
	}

	@Override
	public boolean branches() {
		return false;
	}

	@Override
	public boolean fallsThrough() {
		return true;
	}

	@Override
	public List<ValueBox> getUseBoxes() {
		final ArrayList<ValueBox> useBoxes = new ArrayList<>();
		useBoxes.add(conditionBox);
		useBoxes.addAll(conditionBox.getValue().getUseBoxes());

		return useBoxes;
	}

	@Override
	public String toString() {
		final InlineUnitPrinter printer = new InlineUnitPrinter();
		toString(printer);

		return printer.toString();
	}

}
