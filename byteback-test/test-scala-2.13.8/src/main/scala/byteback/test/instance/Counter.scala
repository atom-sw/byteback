/**
  * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
  */
package byteback.test.instance;

import byteback.specification.Contract._;
import byteback.specification.Special._;
import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};

import scala.annotation.meta._;

class Counter {

  @Behavior
  def sets_count(c: Int): Boolean = {
    return equal(count, c);
  }

  @(Ensure @setter)("sets_count")
  @(Return @setter)
  @(Behavior @getter)
  var count: Int = 0;

  @TwoState
  @Behavior
  def increments_count_by_1(): Boolean = {
    return equal(count, old(count) + 1);
  }

  @TwoState
  @Behavior
  def increments_count_by_10(): Boolean = {
    return equal(count, old(count) + 10);
  }

  @Ensure("increments_count_by_1")
  @Return
  def increment() : Unit = {
    count = count + 1;
  }

  @Ensure("increments_count_by_10")
  @Return
  def incrementTo10(): Unit = {
    var i: Int = 0;

    while (i < 10) {
      invariant(lte(0, i) & lte(i, 10));
      invariant(equal(count, old(count) + i))
      increment();
      i = i + 1;
    }
  }

}
/**
  * RUN: %{verify} %t.bpl | filecheck %s
  * CHECK: Boogie program verifier finished with 4 verified, 0 errors
  */
