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

    @TwoState
    @Behavior
    public static boolean reverse_of_old(final int[] a) {
        int i = Bindings.integer();
        return forall(i, implies(lte(0, i) & lt(i, a.length), eq(a[i], old(a[a.length - 1 - i]))));
    }

    @Behavior
    public static boolean bounded_index(final int[] a, final int i) {
        return lte(0, i) & lt(i, a.length);
    }

    @Behavior
    public static boolean bounded_indices(final int[] a, final int i, final int j) {
        return bounded_index(a, i) & bounded_index(a, j);
    }

    @TwoState
    @Behavior
    public static boolean swapped_elements(final int[] a, final int i, final int j) {
        return eq(old(a[i]), a[j]) & eq(old(a[j]), a[i]);
    }

    @Return
    @Require("bounded_indices")
    @Ensure("swapped_elements")
    public static void swap(final int[] a, int i, int j) {
        final int y = a[i];
        a[i] = a[j];
        a[j] = y;
    }

    @Behavior
    public static boolean array_is_not_null(int[] a) {
        return neq(a, null);
    }

	@TwoState
    @Behavior
    public static boolean reversed(int[] a) {
        return reverse_of_old(a);
    }

    @Require("array_is_not_null")
    @Ensure("reversed")
    public static void reverse(int[] a) {
        final int l = a.length - 1;
        int i = 0;

        while (i < (l - i)) {
            final int k = Bindings.integer();
            invariant(forall(k, implies((lte(0, k) & lt(k, i)) | (lt(l - i, k) & lte(k, l)), eq(a[k], old(a[l - k])))));
            invariant(forall(k, implies(lte(i, k) & lt(k, l - i), eq(a[k], old(a[k])))));
            invariant(lte(0, i) & lte(i, (l + 1) / 2));
            swap(a, i, l - i);
            ++i;
        }
    }

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
