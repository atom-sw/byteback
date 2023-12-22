package byteback.analysis;

import soot.Unit;
import soot.jimple.StmtSwitch;

public interface LogicStmtSwitch<T> extends StmtSwitch, LogicStmtVisitor<T> {

	@Override
	default void defaultCase(final Object object) {
		defaultCase((Unit) object);
	}

}
