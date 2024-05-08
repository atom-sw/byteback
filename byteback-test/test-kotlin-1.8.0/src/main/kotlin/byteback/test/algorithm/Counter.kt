/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm

import byteback.specification.Contract.*
import byteback.specification.Operators.*
import byteback.specification.Special.*

class Counter {

  @Behavior
  fun `sets count`(c: Int): Boolean {
    return eq(count, c)
  }

  @set:Ensure("sets count")
	@set:Return
	@get:Behavior
	var count: Int = 0

  @TwoState
  @Behavior
  fun `increments count by 1`(): Boolean {
    return eq(count, old(count) + 1)
  }

  @TwoState
  @Behavior
  fun `increments count by 10`(): Boolean {
    return eq(count, old(count) + 10)
  }

  @Ensure("increments count by 1")
  @Return
  fun increment(): Unit {
    count = count + 1
  }

  @Ensure("increments count by 10")
  @Return
  fun incrementTo10(): Unit {
    var i: Int = 0

    while (i < 10) {
      invariant(lte(0, i) and lte(i, 10))
      invariant(eq(count, old(count) + i))
      increment()
      i = i + 1
    }
  }
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
