/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import byteback.specification.Bindings;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Quantifiers.forall;
import static byteback.specification.Special.old;

// Taken from the following Dafny implementation:
// https://gist.github.com/Karneades/cd5f1d283e07be858e833b9463c16ab2
public class ArrayReverse {

	@Behavior
	public static boolean array_is_not_null(int[] a) {
		return neq(a, null);
	}

	@TwoState
	@Behavior
	public static boolean reversed(int[] a) {
		int k = Bindings.integer();

		return forall(k, implies(lte(0, k) & lt(k, a.length), eq(a[k], old(a[(a.length - 1) - k]))));
	}

	@Require("array_is_not_null")
	@Ensure("reversed")
	public static void reverse(int[] a) {
		final int l = a.length - 1;
		int i = 0;

		while (i < (l - i)) {
			invariant(lte(0, i) & lte(i, (l + 1) / 2));
			final int k = Bindings.integer();
			invariant(forall(k, implies((lte(0, k) & lt(k, i)) | (lt(l - i, k) & lte(k, l)), eq(a[k], old(a[l - k])))));
			invariant(forall(k, implies(lte(i, k) & lt(k, l - i), eq(a[k], old(a[k])))));
			
			final int j = l - i;
			final int y = a[i];
			a[i] = a[j];
			a[j] = y;

			++i;
		}
	}

}
