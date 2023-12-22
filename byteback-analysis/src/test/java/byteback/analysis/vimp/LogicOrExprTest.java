package byteback.analysis.vimp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import byteback.analysis.LogicExprSwitch;
import org.junit.Test;

public class LogicOrExprTest {

	@Test
	public void Apply_DefaultLogicExprSwitch_CallsCaseMethod() {
		final LogicExpr a = mock(LogicExpr.class);
		final LogicExpr b = mock(LogicExpr.class);
		final var v = new LogicOrExpr(a, b);
		@SuppressWarnings("rawtypes")
		final LogicExprSwitch sw = mock(LogicExprSwitch.class);
		v.apply(sw);
		verify(sw).caseLogicOrExpr(v);
	}

}
