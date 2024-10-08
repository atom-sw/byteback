/** 
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl	
 */
package byteback.test.exceptions

import byteback.specification.Contract.*
import byteback.specification.Operators.*

class Basic {

  @get:Behavior val f: Int = 0

  fun tryCatchBlock(): Exception {
    try {
      throw Exception()
    } catch (e: Exception) {
      return e
    }
  }

  @Return
	fun neverThrows(): Unit {}

  fun neverCatches(): Unit {
    try {
      neverThrows()
    } catch (e: Exception) {
      assertion(false)
    }
  }

  @Behavior
  fun `always throws`(): Boolean {
    return true
  }

  @Raise(`exception` = Exception::class, `when` = "always throws")
  fun alwaysThrows(): Unit {
    throw Exception()
  }

  fun alwaysCatches(): Unit {
    try {} catch (e: Exception) {}
  }

  fun callsAlwaysThrows(): Unit {
    try {
      alwaysThrows()
      assertion(false)
    } catch (e: Exception) {}
  }

  @Behavior
  fun `argument is even`(n: Int): Boolean {
    return eq(n % 2, 0)
  }

  @Raise(`exception` = Exception::class, `when` = "argument is even")
  fun throwsIfEven(n: Int): Unit {
    if (n % 2 == 0) {
      throw Exception()
    }
  }

  fun catchesIfEven(): Unit {
    try {
      throwsIfEven(2)
      assertion(false)
    } catch (e: Exception) {}
  }

  @Behavior
  fun `f is 1`(): Boolean {
    return eq(f, 1)
  }

  @Behavior
  fun `f is 2`(): Boolean {
    return eq(f, 2)
  }

  @Behavior
  fun `f is 3`(): Boolean {
    return eq(f, 3)
  }

  @Behavior
  fun `f is 4`(): Boolean {
    return eq(f, 4)
  }

  @Behavior
  fun `f is gt 4`(): Boolean {
    return gt(f, 4)
  }

  @Raise(`exception` = Exception1::class, `when` = "f is 1")
  @Raise(`exception` = Exception2::class, `when` = "f is 2")
  @Raise(`exception` = Exception3::class, `when` = "f is 3")
  @Raise(`exception` = Exception4::class, `when` = "f is 4")
  @Return(`when` = "f is gt 4")
  fun throwsMultiple(): Unit {
    if (f == 1) {
      throw Exception1()
    } else if (f == 2) {
      throw Exception2()
    } else if (f == 3) {
      throw Exception3()
    } else if (f == 4) {
      throw Exception4()
    }
  }
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 10 verified, 0 errors
 */
