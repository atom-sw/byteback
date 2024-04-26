/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$Die -c %{class}$FixedDie -o %t.bpl
 */
package byteback.test.instance;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;

public class Dice {

	public interface Die {

		@Behavior
		default boolean outcome_is_positive(int max, int outcome) {
			return lte(1, outcome);
		}

		@Behavior
		default boolean outcome_is_leq_max(int max, int outcome) {
			return lte(outcome, max);
		}

		@Ensure("outcome_is_positive")
		@Ensure("outcome_is_leq_max")
		int roll(int max);

	}

	public static class FixedDie implements Die {

		@Behavior
		public boolean result_is_max(int max, int returns) {
			return eq(max, returns);
		}

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
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
