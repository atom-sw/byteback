/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c '%{class}$Die' -c '%{class}$FixedDie' -o %t.bpl
 */
package byteback.test.instance;

import byteback.specification.Contract._;
import byteback.specification.Special._;
import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};

class Dice {

  abstract class Die {

    @Behavior
    def outcome_is_positive(max: Int, outcome: Int): Boolean = {
      return lte(1, outcome);
    }

    @Behavior
    def outcome_is_leq_max(max: Int, outcome: Int): Boolean = {
      return lte(outcome, max);
    }

    @Behavior
    def max_is_positive(max: Int): Boolean = {
      return gte(max, 1);
    }

    @Require("max_is_positive")
    @Ensure("outcome_is_positive")
    @Ensure("outcome_is_leq_max")
    def roll(max: Int): Int

  }

  class FixedDie extends Die {

    @Behavior
    def result_is_max(max: Int, returns: Int): Boolean = {
      return equal(max, returns);
    }

    @Ensure("result_is_max")
    def roll(max: Int): Int = {
      return max;
    }

  }

  def main(): Unit = {
    var die: FixedDie = new FixedDie();
    var max: Int = 6;
    var result: Int = die.roll(max);

    assertion(lte(result, max));
  }

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
