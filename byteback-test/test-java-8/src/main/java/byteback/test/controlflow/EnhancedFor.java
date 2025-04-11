/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.controlflow;

import byteback.specification.Bindings;
import static byteback.specification.Quantifiers.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Contract.*;

public class EnhancedFor {

	@Behavior
	public static boolean contains(int[] a, int x) {
		final int i = Bindings.integer();

		return exists(i, lte(0, i) & lt(i, a.length) & eq(a[i], x));
	}

	public static void forEach(int[] a) {
		for (int x : a) {
			assertion(contains(a, x));
		}
	}

}
/**
 * RUN: %{verify} /infer:j %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
