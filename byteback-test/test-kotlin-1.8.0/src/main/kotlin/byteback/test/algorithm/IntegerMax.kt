/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --pei --strict -o %t.bpl
 */
package byteback.test.algorithm

import byteback.specification.Contract.*
import byteback.specification.Operators.*
import byteback.specification.Quantifiers.*

import byteback.specification.Bindings

object IntegerMax {

    @Behavior
    fun `array is not null`(a: IntArray): Boolean {
        return neq(a, null)
    }

    @Behavior
    fun `max in range`(a: IntArray, n: Int, start: Int, end: Int): Boolean {
        val i = Bindings.integer()
        return forall(i, implies(lte(start, i) and lt(i, end), gte(n, a[i])))
    }

    @Behavior
    fun `array is not empty`(a: IntArray): Boolean {
        return gt(a.size, 0)
    }

    @Behavior
    fun `result is max`(a: IntArray, t: Int): Boolean {
        return `max in range`(a, t, 0, a.size)
    }

    @Require("array is not null")
    @Require("array is not empty")
    @Ensure("result is max")
		@Return
    fun max(a: IntArray): Int {
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
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
