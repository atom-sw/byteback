/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --iobe --strict -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Return;
import static byteback.specification.Operators.*;

public class FailOnPotentialIndexOutOfBounds {

	@Return
	public static void passedTarget(int[] a) {
		if (a.length > 0) {
			int a_0 = a[0];
		}
	}

	@Return
	public static void passedIndex(int i) {
		int[] a = new int[10];

		if (i > 0 && i < a.length) {
			int a_i = a[i];
		}
	}

	@Behavior
	public static boolean array_is_not_empty(int[] a) {
		return gt(a.length, 0);
	}

	@Require("array_is_not_empty")
	public static void constrainedTarget(int[] a) {
		int a_0 = a[0];
	}

	@Return
	public static void passedArrayAndIndex(int[] a, int i) {
		if (a.length > 0 && i >= 0 && i < a.length)  {
			int a_i = a[i];
		}
	}

	@Behavior
	public static boolean array_is_not_empty_and_index_is_in_bounds(int[] a, int i) {
		return gt(a.length, 0) & lte(0, i) & lt(i, a.length);
	}

	@Require("array_is_not_empty_and_index_is_in_bounds")
	public static void constrainedTargetAndIndex(int[] a, int i) {
		int a_i = a[i];
	}

	@Raise(exception = IndexOutOfBoundsException.class)
	public static void expectedIndexOutOfBounds1(int[] a) {

		// CHECK: Error: this assertion could not be proved
		int a_m1 = a[-1];
	}

	@Raise(exception = IndexOutOfBoundsException.class)
	public static void expectedIndexOutOfBounds2(int[] a) {

		// CHECK: Error: this assertion could not be proved
		int a_l = a[a.length];
	}

	@Behavior
	public static boolean array_is_empty(int[] a) {
		return eq(a.length, 0);
	}

	@Raise(exception = IndexOutOfBoundsException.class, when = "array_is_empty")
	public static void expectedIndexOutOfBounds3(int[] a) {
		// CHECK: Error: this assertion could not be proved
		int a_0 = a[0];
	}

	@Behavior
	public static boolean index_is_negative(int i) {
		return lt(i, 0);
	}

	@Raise(exception = IndexOutOfBoundsException.class, when = "index_is_negative")
	public static void expectedIndexOutOfBounds4(int i) {
		// CHECK: Error: this assertion could not be proved
		int[] a = new int[10];
		int a_i = a[i];
	}

	@Behavior
	public static boolean array_is_empty_or_index_is_negative(int[] a, int i) {
		return array_is_empty(a) | index_is_negative(i);
	}

	@Raise(exception = IndexOutOfBoundsException.class, when = "array_is_empty_or_index_is_negative")
	public static void expectedIndexOutOfBounds5(int[] a, int i) {
		// CHECK: Error: this assertion could not be proved
		int a_i = a[i];
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 6 verified, 5 errors
 */
