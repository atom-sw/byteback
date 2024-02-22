package byteback.analysis.body.vimp;

import soot.Unit;
import soot.jimple.StmtSwitch;

public interface VimpStmtSwitch<T> extends StmtSwitch, byteback.analysis.body.vimp.visitor.VimpStmtSwitch<T> {

	@Override
	default void defaultCase(final Object object) {
		defaultCase((Unit) object);
	}

}
