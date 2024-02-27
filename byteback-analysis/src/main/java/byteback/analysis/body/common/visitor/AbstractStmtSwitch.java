package byteback.analysis.body.common.visitor;

import soot.Body;
import soot.Unit;
import soot.jimple.Stmt;

public abstract class AbstractStmtSwitch<R> extends soot.jimple.AbstractStmtSwitch<R> {

	@Override
	public void defaultCase(final Object o) {
		defaultCase((Stmt) o);
	}

	public void defaultCase(final Stmt s) {
	}

	public R visit(final Unit unit) {
		unit.apply(this);

		return this.getResult();
	}

	public R visit(final Body body) {
		for (final Unit unit : body.getUnits()) {
			unit.apply(this);
		}

		return this.getResult();
	}

}
