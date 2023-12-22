/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import static byteback.annotations.Quantifier.*;

import byteback.annotations.Binding;
import byteback.annotations.Contract.Ensure;

public class DoubleSum {

	@Pure
	public static boolean positive_arguments_imply_positive_sum(double[] as, double ret) {
		return implies(positive_arguments(as), gte(ret, 0));
	}

	@Pure
	public static boolean positive_arguments(double[] as) {
		int i = Binding.integer();

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
