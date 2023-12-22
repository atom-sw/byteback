package byteback.analysis.transformer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.StaticInvokeExpr;

public class TransformerFixture {

	public static StaticInvokeExpr mockStaticInvokeExpr(final String className, final String methodName,
			final Value[] values) {
		final SootClass clazz = mock(SootClass.class);
		when(clazz.getName()).thenReturn(className);
		final SootMethod method = mock(SootMethod.class);
		when(method.getName()).thenReturn(methodName);
		when(method.getDeclaringClass()).thenReturn(clazz);
		final StaticInvokeExpr invokeValue = mock(StaticInvokeExpr.class);
		when(invokeValue.getArgCount()).thenReturn(1);

		for (int i = 0; i < values.length; ++i) {
			when(invokeValue.getArg(i)).thenReturn(values[i]);
		}

		when(invokeValue.getMethod()).thenReturn(method);
		return invokeValue;
	}

}
