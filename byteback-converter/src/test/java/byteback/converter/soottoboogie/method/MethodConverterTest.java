package byteback.converter.soottoboogie.method;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import soot.SootMethod;
import soot.Type;

public class MethodConverterTest {

	@Test
	public void MethodName_GivenConstructorWithoutParameters_ReturnsValidName() {
		final SootMethod method = mock(SootMethod.class, Mockito.RETURNS_DEEP_STUBS);
		when(method.getDeclaringClass().getName()).thenReturn("dummy.Test");
		when(method.getName()).thenReturn("<init>");
		when(method.getParameterTypes()).thenReturn(Collections.emptyList());
		when(method.getReturnType().toString()).thenReturn("void");
		assertEquals(MethodConverter.methodName(method), "dummy.Test.$init$#void##");
	}

	@Test
	public void MethodName_GivenMethodWithoutParameters_ReturnsValidName() {
		final SootMethod method = mock(SootMethod.class, Mockito.RETURNS_DEEP_STUBS);
		when(method.getDeclaringClass().getName()).thenReturn("dummy.Test");
		when(method.getName()).thenReturn("test");
		when(method.getParameterTypes()).thenReturn(Collections.emptyList());
		when(method.getReturnType().toString()).thenReturn("void");
		assertEquals(MethodConverter.methodName(method), "dummy.Test.test#void##");
	}

	@Test
	public void MethodName_GivenMethodWithIntArrayParameter_ReturnsValidName() {
		final SootMethod method = mock(SootMethod.class, Mockito.RETURNS_DEEP_STUBS);
		final Type type = mock(Type.class);
		when(type.toString()).thenReturn("int[]");
		when(method.getDeclaringClass().getName()).thenReturn("dummy.Test");
		when(method.getName()).thenReturn("test");
		when(method.getParameterTypes()).thenReturn(List.of(type));
		when(method.getReturnType().toString()).thenReturn("void");
		assertEquals(MethodConverter.methodName(method), "dummy.Test.test#void#int?##");
	}

	@Test
	public void MethodName_GivenMethodWithIntegerParameter_ReturnsValidName() {
		final SootMethod method = mock(SootMethod.class, Mockito.RETURNS_DEEP_STUBS);
		final Type type = mock(Type.class);
		when(type.toString()).thenReturn("int");
		when(method.getDeclaringClass().getName()).thenReturn("dummy.Test");
		when(method.getName()).thenReturn("test");
		when(method.getParameterTypes()).thenReturn(List.of(type));
		when(method.getReturnType().toString()).thenReturn("void");
		assertEquals(MethodConverter.methodName(method), "dummy.Test.test#void#int##");
	}

}
