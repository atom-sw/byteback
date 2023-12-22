/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import static byteback.annotations.Quantifier.*;
import byteback.annotations.Binding;

public class SquareSortedArray {

	@Pure
	public static boolean sorted(final int[] a, final int i, final int j) {
		final int k = Binding.integer();

		return forall(k, implies(lt(i, k) & lt(k, j), lte(a[k - 1], a[k])));
	}

	@Predicate
	public static boolean array_is_null(final int[] a) {
		return eq(a, null);
	}

	@Predicate
	public static boolean array_is_sorted(final int[] a) {
		return sorted(a, 0, a.length);
	}

	@Predicate
	public static boolean array_is_sorted(final int[] a, final int[] b) {
		return sorted(b, 0, b.length);
	}

	@Predicate
	public static boolean array_is_not_empty(final int[] a) {
		return gt(a.length, 0);
	}

	@Raise(exception = NullPointerException.class, when = "array_is_null")
	@Require("array_is_sorted")
	public static int[] squareSortedArray(final int[] a) {

		if (a == null) {
			throw new NullPointerException();
		}

		final int[] b = new int[a.length];
		int i = 0;
		int j = a.length - 1;
		int c = a.length - 1;

		while (i <= j) {
			invariant(lte(j - i, a.length));
			invariant(eq(a.length, b.length));

			int iq = a[i] * a[i];
			int jq = a[j] * a[j];

			if (iq < jq) {
				b[c] = jq;
				j -= 1;
			} else {
				b[c] = iq;
				i += 1;
			}


			c -= 1;
		}

		return b;
	}
	
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
