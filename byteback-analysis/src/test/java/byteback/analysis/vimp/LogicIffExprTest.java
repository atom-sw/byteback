package byteback.analysis.vimp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import byteback.analysis.body.vimp.LogicExpr;
import byteback.analysis.body.vimp.LogicIffExpr;
import byteback.analysis.body.vimp.visitor.LogicExprSwitch;
import org.junit.Test;

public class LogicIffExprTest {

	@Test
	public void Apply_DefaultLogicExprSwitch_CallsCaseMethod() {
		final LogicExpr a = mock(LogicExpr.class);
		final LogicExpr b = mock(LogicExpr.class);
		final var v = new LogicIffExpr(a, b);
		@SuppressWarnings("rawtypes")
		final LogicExprSwitch sw = mock(LogicExprSwitch.class);
		v.apply(sw);
		verify(sw).caseLogicIffExpr(v);
	}

}
