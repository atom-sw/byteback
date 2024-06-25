/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --dbz -o %t.bpl
 */
package byteback.test.exceptions

import byteback.specification.Contract.Behavior
import byteback.specification.Contract.Raise
import byteback.specification.Contract.Require
import byteback.specification.Contract.Return
import byteback.specification.Contract.Returns

import byteback.specification.Operators._
import byteback.specification.Operators.{eq => equal}

class PotentialDivisionByZero {

  @Behavior
  def n_is_zero(n: Int): Boolean = {
    equal(n, 0)
  }

  @Behavior
  def n_is_not_zero(n: Int): Boolean = {
    neq(n, 0)
  }

  @Behavior
  def always(): Boolean = {
    true
  }

  @Return(when = "n_is_not_zero")
  @Raise(when = "n_is_zero", exception = classOf[ArithmeticException])
  def potentialDivisionByZero(n: Int): Int = {
    10 / n
  }

  @Raise(exception = classOf[ArithmeticException], when = "always")
  def divisionByZero(): Int = {
    10 / 0
  }

  @Return
  def validFloatDivision(n: Float): Float = {
    10 / n
  }

  @Return
  def validDoubleDivision(n: Double): Double = {
    10 / n
  }

  @Return
  def validDivisionByZero(n: Int): Int = {
    if (n != 0) {
      10 / n
    } else {
      0
    }
  }

  @Return(when = "always")
  def caughtDivisionByZero(): Int = {
    try {
      10 / 0
    } catch {
      case exception: ArithmeticException => 0
    }
  }
}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
