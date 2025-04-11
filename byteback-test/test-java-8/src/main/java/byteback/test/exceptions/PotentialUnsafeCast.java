/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --cce -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Raises;
import byteback.specification.Contract.Return;
import static byteback.specification.Operators.*;

@SuppressWarnings("unused")
public class PotentialUnsafeCast {

	public static class A {

		@Return
		public A() {}

	}

	public static class B extends A {

		@Return
		public B() {}

	}

	public static class C {

		@Return
		public C() {}

	}

	@Behavior
	public boolean x_is_instanceof_A(final Object x) {
		return x instanceof A;
	}

	@Behavior
	public boolean x_is_not_instanceof_A(final Object x) {
		return not(x_is_instanceof_A(x));
	}

	@Return(when = "x_is_instanceof_A")
	public void unsafeDownCast(final Object x) {
		A a = (A) x;
	}

	@Return
	public void safeDownCast(final Object x) {
		if (x instanceof A) {
			A a = (A) x;
		}
	}

	@Return
	public void safeUpcast(final B b) {
		A a = (A) b;
	}

	@Behavior
	public boolean always() {
		return true;
	}

	@Raise(exception = ClassCastException.class, when = "always")
	public void invalidDownCast() {
		A a = new A();
		B b = (B) a;
	}

	@Return
	public void validCastNull() {
		A a = (A) null;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 6 verified, 0 errors
 */
