/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.examples;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import static byteback.annotations.Quantifier.*;

import byteback.annotations.Binding;

public class Summary {

	@Pure
	public static boolean contains(int[] as, int e, int from, int to) {
		int i = Binding.integer();

		return exists(i, lte(from, i) & lt(i, to) & eq(as[i], e));
	}

	@Predicate
	public static boolean values_do_not_contain_1(int[] values) {
		return not(contains(values, 1, 0, values.length));
	}

	@Predicate
	public static boolean result_is_nonnegative(int[] values, int result) {
		return gte(result, 0);
	}

	@Require("values_do_not_contain_1")
	@Ensure("result_is_nonnegative")
	@Return
	public static int summary(int... values) {
		var result = 0;

		for (var value : values) {
			invariant(gte(result, 0));

			result += switch (value) {
				case 0:
					yield (1);
				case 1:
					yield (-1);
				default:
					if (value > 0)
						yield (value);
					else
						yield (0);
			};
		}

		return result;
	}

}
/**
 * RUN: %{verify} /infer:j %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
