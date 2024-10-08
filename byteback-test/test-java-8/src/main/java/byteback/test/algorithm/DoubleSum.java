/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Quantifiers.*;

import byteback.specification.Bindings;
import byteback.specification.Contract.Ensure;

public class DoubleSum {

	@Behavior
	public static boolean positive_arguments_imply_positive_sum(double[] as, double ret) {
		return implies(positive_arguments(as), gte(ret, 0));
	}

	@Behavior
	public static boolean positive_arguments(double[] as) {
		int i = Bindings.integer();

		return forall(i, implies(lt(i, as.length), gt(as[i], 0)));
	}

	@Ensure("positive_arguments_imply_positive_sum")
	public static double sum(double[] as) {
		double sum = 0;

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
