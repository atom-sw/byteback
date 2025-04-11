/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */

package byteback.test.algorithm;

import byteback.specification.Operators.*;
import byteback.specification.Contract.*;

public class McCarthy91 {

	@Behavior
	fun `returns 91 with n lte 100`(n: Int, returns: Int): Boolean {
		return implies(lte(n, 100), eq(returns, 91));
	}

	@Behavior
	fun `returns n min 10 with n gt 100`(n: Int, returns: Int): Boolean {
		return implies(gt(n, 100), eq(returns, n - 10));
	}

	@Return
	@Ensure("returns 91 with n lte 100")
	@Ensure("returns n min 10 with n gt 100")
	fun mcCarthy(n: Int): Int {
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
