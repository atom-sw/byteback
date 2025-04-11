/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */

package byteback.test.algorithm;

import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};
import byteback.specification.Contract._;

class McCarthy91 {

	@Behavior
	def returns_91_with_n_lte_100(n: Int, returns: Int): Boolean = {
		return implies(lte(n, 100), equal(returns, 91));
	}

	@Behavior
	def returns_n_min_10_with_n_gt_100(n: Int, returns: Int): Boolean = {
		return implies(gt(n, 100), equal(returns, n - 10));
	}

	@Return
	@Ensure("returns_91_with_n_lte_100")
	@Ensure("returns_n_min_10_with_n_gt_100")
	def mcCarthy(n: Int): Int = {
		if (n > 100) {
			return n - 10;
		}

		return mcCarthy(mcCarthy(n + 11));
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
