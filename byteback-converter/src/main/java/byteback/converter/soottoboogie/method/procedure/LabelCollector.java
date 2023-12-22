package byteback.converter.soottoboogie.method.procedure;

import byteback.analysis.JimpleStmtSwitch;
import byteback.converter.soottoboogie.Convention;
import byteback.converter.soottoboogie.ConversionException;
import byteback.frontend.boogie.ast.Label;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import soot.Body;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.TableSwitchStmt;

public class LabelCollector extends JimpleStmtSwitch<Map<Unit, Label>> {

	private int counter;

	private final Map<Unit, Label> labels;

	public LabelCollector() {
		this.counter = 0;
		this.labels = new HashMap<>();
	}

	public boolean hasLabel(final Unit unit) {
		return labels.containsKey(unit);
	}

	public Optional<Label> getLabel(final Unit unit) {
		return Optional.ofNullable(labels.get(unit));
	}

	public Label fetchLabel(final Unit unit) {
		return getLabel(unit).orElseThrow(() -> new ConversionException("Cannot fetch label for target " + unit));
	}

	public void branchTo(final Unit target) {
		labels.put(target, Convention.makeLabelStatement(++counter));
	}

	public void collect(final Body body) {
		visit(body);
	}

	@Override
	public void caseLookupSwitchStmt(final LookupSwitchStmt switchStatement) {
		for (final Unit target : switchStatement.getTargets()) {
			branchTo(target);
		}

		branchTo(switchStatement.getDefaultTarget());
	}

	@Override
	public void caseTableSwitchStmt(final TableSwitchStmt switchStatement) {
		for (final Unit target : switchStatement.getTargets()) {
			branchTo(target);
		}

		branchTo(switchStatement.getDefaultTarget());
	}

	@Override
	public void caseIfStmt(final IfStmt ifStatement) {
		branchTo(ifStatement.getTarget());
	}

	@Override
	public void caseGotoStmt(final GotoStmt gotoStatement) {
		branchTo(gotoStatement.getTarget());
	}

	@Override
	public Map<Unit, Label> result() {
		return labels;
	}

}
