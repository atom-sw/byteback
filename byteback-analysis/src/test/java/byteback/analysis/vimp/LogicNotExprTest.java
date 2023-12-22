package byteback.analysis.vimp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import byteback.analysis.LogicExprSwitch;
import org.junit.Test;

public class LogicNotExprTest {

	@Test
	public void Apply_DefaultLogicExprSwitch_CallsCaseMethod() {
		final LogicExpr a = mock(LogicExpr.class);
		final var v = new LogicNotExpr(a);
		@SuppressWarnings("rawtypes")
		final LogicExprSwitch sw = mock(LogicExprSwitch.class);
		v.apply(sw);
		verify(sw).caseLogicNotExpr(v);
	}

}
