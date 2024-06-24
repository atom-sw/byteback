/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --nas -o %t.bpl
 */
package byteback.test.exceptions

import byteback.specification.Contract.assertion
import byteback.specification.Operators._

import byteback.specification.Contract.Behavior
import byteback.specification.Contract.Raise
import byteback.specification.Contract.Return

class PotentialNegativeArraySize {

  @Behavior
  def always(n: Int): Boolean = {
    true
  }

  @Behavior
  def n_is_negative(n: Int): Boolean = {
    lt(n, 0)
  }

  @Behavior
  def n_is_positive(n: Int): Boolean = {
    gte(n, 0)
  }

  @Raise(exception = classOf[NegativeArraySizeException], when = "n_is_negative")
  @Return(when = "n_is_positive")
  def potentialNegativeArray(n: Int): Unit = {
    val a = new Array[Int](n)
  }

  @Return
  def safeArrayInstantiation(n: Int): Unit = {
    if (n >= 0) {
      val a = new Array[Int](n)
    }
  }

  @Raise(exception = classOf[NegativeArraySizeException], when = "always")
  def incorrectArrayInstantiation(n: Int): Unit = {
    val a = new Array[Int](-1)
  }

  @Return
  def CatchNegativeArraySizeException_WrappingNegativeArrayInstantiation_AlwaysEntersCatch(): Unit = {
    try {
      val a = new Array[Int](-1)
    } catch {
      case exception: NegativeArraySizeException => return
    }
    assertion(false)
  }

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
