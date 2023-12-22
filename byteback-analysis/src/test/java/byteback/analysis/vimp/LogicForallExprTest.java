package byteback.analysis.vimp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import byteback.analysis.LogicExprSwitch;
import org.junit.Test;
import soot.Local;
import soot.util.Chain;

public class LogicForallExprTest {

	@Test
	public void Apply_DefaultLogicExprSwitch_CallsCaseMethod() {
		final LogicExpr a = mock(LogicExpr.class);
		@SuppressWarnings("unchecked")
		final Chain<Local> b = mock(Chain.class);
		final var v = new LogicForallExpr(b, a);
		@SuppressWarnings("rawtypes")
		final LogicExprSwitch sw = mock(LogicExprSwitch.class);
		v.apply(sw);
		verify(sw).caseLogicForallExpr(v);
	}

}
