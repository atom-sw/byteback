/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --nas -o %t.bpl
 */
package byteback.test.exceptions

import byteback.specification.Contract.assertion
import byteback.specification.Operators.*

import byteback.specification.Contract.Behavior
import byteback.specification.Contract.Raise
import byteback.specification.Contract.Return

class PotentialNegativeArraySize {

    @Behavior
    fun `always`(n: Int): Boolean {
        return true
    }

    @Behavior
    fun `n is negative`(n: Int): Boolean {
        return lt(n, 0)
    }

    @Behavior
    fun `n is positive`(n: Int): Boolean {
        return gte(n, 0)
    }

    @Raise(`exception` = NegativeArraySizeException::class, `when` = "n is negative")
    @Return(`when` = "n is positive")
    fun potentialNegativeArray(n: Int) {
        val a = IntArray(n)
    }

    @Return
    fun safeArrayInstantiation(n: Int) {
        if (n >= 0) {
            val a = IntArray(n)
        }
    }

    @Raise(`exception` = NegativeArraySizeException::class, `when` = "always")
    fun incorrectArrayInstantiation(n: Int) {
        val a = IntArray(-1)
    }

		@Return
    fun `Catch NegativeArraySizeException Wrapping NegativeArrayInstantiation Always Enters Catch`() {
        try {
            val a = IntArray(-1)
        } catch (exception: NegativeArraySizeException) {
            return
        }
        assertion(false)
    }
}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
