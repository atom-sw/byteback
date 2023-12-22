/**
  * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
  */
package byteback.test.algorithm;

import byteback.annotations.Contract.*
import byteback.annotations.Special.*
import byteback.annotations.Operator.*

class Counter {

  @Predicate
  fun `sets count`(c: Int): Boolean {
    return eq(count, c);
  }

	@set:Ensure("sets count")
  @set:Return
	@get:Pure
  var count: Int = 0;

  @Predicate
  fun `increments count by 1`(): Boolean {
    return eq(count, old(count) + 1);
  }

  @Predicate
  @Return
  fun `increments count by 10`(): Boolean {
    return eq(count, old(count) + 10);
  }

  @Ensure("increments count by 1")
  @Return
  fun increment() : Unit {
    count = count + 1;
  }

  @Ensure("increments count by 10")
  @Return
  fun incrementTo10(): Unit {
    var i: Int = 0;

    while (i < 10) {
      invariant(lte(0, i) and lte(i, 10));
      invariant(eq(count, old(count) + i))
      increment();
      i = i + 1;
    }
  }

}
/**
  * RUN: %{verify} %t.bpl | filecheck %s
  * CHECK: Boogie program verifier finished with 4 verified, 0 errors
  */
