package byteback.analysis.vimp;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import soot.Local;
import soot.util.Chain;

public class QuantifierExprTest {

	public void Init_LogicForallExprWithoutFreeLocals_ThrowsIllegalArgumentException() {
		final Local local = mock(Local.class);
		@SuppressWarnings("unchecked")
		final Chain<Local> locals = mock(Chain.class);
		when(locals.isEmpty()).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> new LogicForallExpr(locals, local));
	}

	public void Init_LogicExistsExprWithoutFreeLocals_ThrowsIllegalArgumentException() {
		final Local local = mock(Local.class);
		@SuppressWarnings("unchecked")
		final Chain<Local> locals = mock(Chain.class);
		when(locals.isEmpty()).thenReturn(true);

		assertThrows(IllegalArgumentException.class, () -> new LogicExistsExpr(locals, local));
	}

}
