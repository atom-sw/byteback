/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --pei --strict -o %t.bpl
 */
package byteback.test.algorithm

import byteback.specification.Contract.*
import byteback.specification.Operators.*
import byteback.specification.Quantifiers.*
import byteback.specification.Bindings

class DoubleMax {

    @Behavior
    fun `array is not null`(a: DoubleArray): Boolean {
        return neq(a, null)
    }

    @Behavior
    fun `max in range`(a: DoubleArray, n: Double, start: Int, end: Int): Boolean {
        val i = Bindings.integer()

        return forall(i, implies(lte(start, i) and lt(i, end), gte(n, a[i])))
    }

    @Behavior
    fun `array is not empty`(a: DoubleArray): Boolean {
        return gt(a.size, 0)
    }

    @Behavior
    fun `result is max`(a: DoubleArray, t: Double): Boolean {
        return `max in range`(a, t, 0, a.size)
    }

    @Require("array is not null")
    @Require("array is not empty")
    @Ensure("result is max")
		@Return
    fun max(a: DoubleArray): Double {
        var t = a[0]
        var i = 1

        while (i < a.size) {
            invariant(lte(0, i) and lte(i, a.size))
            invariant(`max in range`(a, t, 0, i))

            if (a[i] > t) {
                t = a[i]
            }

            i++
        }

        return t
    }
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
