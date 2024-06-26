/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.specification.Operators.eq;
import static byteback.specification.Operators.implies;
import static byteback.specification.Operators.lte;
import static byteback.specification.Operators.gt;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Return;

public class McCarthy91 {

	@Behavior
	public static boolean returns_91_with_n_lte_100(int n, int returns) {
		return implies(lte(n, 100), eq(returns, 91));
	}

	@Behavior
	public static boolean returns_n_min_10_with_n_gt_100(int n, int returns) {
		return implies(gt(n, 100), eq(returns, n - 10));
	}

	@Return
	@Ensure("returns_91_with_n_lte_100")
	@Ensure("returns_n_min_10_with_n_gt_100")
	public static int mcCarthy(int n) {
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
