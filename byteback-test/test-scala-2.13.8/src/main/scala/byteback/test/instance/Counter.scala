/**
  * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
  */
package byteback.test.instance;

import byteback.annotations.Contract._;
import byteback.annotations.Special._;
import byteback.annotations.Operator._;
import byteback.annotations.Operator.{eq => equal};

import scala.annotation.meta._;

class Counter {

  @Predicate
  def sets_count(c: Int): Boolean = {
    return equal(count, c);
  }

  @(Ensure @setter)("sets_count")
  @(Return @setter)
  @(Pure @getter)
  var count: Int = 0;

  @Predicate
  def increments_count_by_1(): Boolean = {
    return equal(count, old(count) + 1);
  }

  @Predicate
  @Return
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
