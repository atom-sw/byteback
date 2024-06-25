/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --dbz -o %t.bpl
 */
package byteback.test.exceptions

import byteback.specification.Contract.Behavior
import byteback.specification.Contract.Raise
import byteback.specification.Contract.Require
import byteback.specification.Contract.Return
import byteback.specification.Contract.Returns

import byteback.specification.Operators.eq
import byteback.specification.Operators.neq

class PotentialDivisionByZero {

    @Behavior
    fun `n is zero`(n: Int): Boolean {
        return eq(n, 0)
    }

    @Behavior
    fun `n is not zero`(n: Int): Boolean {
        return neq(n, 0)
    }

    @Behavior
    fun `always`(): Boolean {
        return true
    }

    @Return(`when` = "n is not zero")
    @Raise(`when` = "n is zero", `exception` = ArithmeticException::class)
    fun potentialDivisionByZero(n: Int): Int {
        return 10 / n
    }

    @Raise(`exception` = ArithmeticException::class, `when` = "always")
    fun divisionByZero(): Int {
        return 10 / 0
    }

    @Return
    fun validFloatDivision(n: Float): Float {
        return 10 / n
    }

    @Return
    fun validDoubleDivision(n: Double): Double {
        return 10 / n
    }

    @Return
    fun validDivisionByZero(n: Int): Int {
        return if (n != 0) {
            10 / n
        } else {
            0
        }
    }

    @Return(`when` = "always")
    fun caughtDivisionByZero(): Int {
        return try {
            10 / 0
        } catch (exception: ArithmeticException) {
            0
        }
    }
}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
