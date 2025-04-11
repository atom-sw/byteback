/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --npe -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;
import static byteback.specification.Operators.*;

@SuppressWarnings("unused")
public class PotentialNullDereference {

	public static class A {

		int field;

		@Return
		public void method() {}

		@Return
		public A() {}

	}

	@Return
	static void freshTarget() {
		A a = new A();

		int a_field = a.field;
		a.field = 0;
		a.method();
	}

	@Return
	static void passedTarget(A a) {
		if (a != null) {
			int a_field = a.field;
			a.field = 0;
			a.method();
		}
	}

	@Behavior
	public static boolean a_is_not_null(A a) {
		return neq(a, null);
	}

	@Return(when = "a_is_not_null")
	static void constrainedPassedTarget(A a) {
		int a_field = a.field;
		a.field = 0;
		a.method();
	}

	@Behavior
	public static boolean a_is_null(A a) {
		return eq(a, null);
	}

	@Raise(exception = NullPointerException.class, when = "a_is_null")
	static void expectedNullPointerException(A a) {
		int a_field = a.field;
		a.field = 0;
		a.method();
	}

	@Return
	static void freshArrayTarget() {
		int[] a = new int[10];
		int a_1 = a[1];
	}

	@Return
	static void passedArrayTarget(int[] a) {
		if (a != null) {
			int a_1 = a[1];
		}
	}

	@Behavior
	public static boolean a_is_not_null(int[] a) {
		return neq(a, null);
	}

	@Return(when = "a_is_not_null")
	static void constrainedPassedArrayTarget(int[] a) {
		int a_1 = a[1];
	}

	@Behavior
	public static boolean a_is_null(int[] a) {
		return eq(a, null);
	}

	@Raise(exception = NullPointerException.class, when = "a_is_null")
	static void expectedArrayNullPointerException(int[] a) {
		int a_1 = a[1];
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 9 verified, 0 errors
 */
