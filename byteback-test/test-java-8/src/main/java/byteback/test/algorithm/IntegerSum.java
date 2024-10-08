/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Quantifiers.*;

import byteback.specification.Bindings;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Behavior;


public class IntegerSum {

	@Behavior
	public static boolean positive_arguments(int[] as) {
		int i = Bindings.integer();

		return forall(i, implies(lt(i, as.length), gt(as[i], 0)));
	}

	@Behavior
	public static boolean array_is_invalid(int[] as) {
		return eq(as, null);
	}

	@Behavior
	public static boolean positive_arguments_imply_positive_sum(int[] as, int ret) {
		return implies(not(array_is_invalid(as)), implies(positive_arguments(as), gte(ret, 0)));
	}

	@Raise(exception = IllegalArgumentException.class, when = "array_is_invalid")
	@Ensure("positive_arguments_imply_positive_sum")
	public static int sum(int[] as) {
		int sum = 0;

		if (as == null) {
			throw new IllegalArgumentException();
		}

		for (int i = 0; i < as.length; ++i) {
			invariant(lte(i, as.length));
			invariant(gte(i, 0));
			invariant(implies(positive_arguments(as), gte(sum, 0)));
			sum += as[i];
		}

		return sum;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
