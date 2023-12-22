package byteback.analysis.vimp;

import java.util.ArrayList;
import java.util.List;
import soot.Value;
import soot.ValueBox;
import soot.grimp.internal.GRValueBox;
import soot.jimple.internal.AbstractStmt;

public abstract class LogicStmt extends AbstractStmt {

	private final ValueBox conditionBox;

	public LogicStmt(final Value condition) {
		this.conditionBox = new GRValueBox(condition);
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

}
