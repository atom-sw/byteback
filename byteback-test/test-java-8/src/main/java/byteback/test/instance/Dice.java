/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c '%{class}$Die' -c '%{class}$FixedDie' -o %t.bpl
 */
package byteback.test.instance;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;

public class Dice {

	public static abstract class Die {

		@Behavior
		public boolean outcome_is_positive(int max, int outcome) {
			return lte(1, outcome);
		}

		@Behavior
		public boolean outcome_is_leq_max(int max, int outcome) {
			return lte(outcome, max);
		}

		@Behavior
		public boolean max_is_positive(int max) {
			return gte(max, 1);
		}

		@Require("max_is_positive")
		@Ensure("outcome_is_positive")
		@Ensure("outcome_is_leq_max")
		public abstract int roll(int max);

	}

	public static class FixedDie extends Die {

		@Behavior
		public boolean result_is_max(int max, int returns) {
			return eq(max, returns);
		}

		@Override
		@Ensure("result_is_max")
		public int roll(int max) {
			return max;
		}

	}

	public static void main() {
		FixedDie die = new FixedDie();
		int max = 6;
		int result = die.roll(max);

		assertion(lte(result, max));
	}
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
