/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm

import byteback.specification.Bindings
import byteback.specification.Contract.*
import byteback.specification.Operators.*
import byteback.specification.Quantifiers.*
import byteback.specification.Special.*

class IntegerSelectionSort {

    @Behavior
    fun `bounded index`(a: DoubleArray, i: Int): Boolean {
        return lte(0, i) and lt(i, a.size)
    }

    @Behavior
    fun `bounded indices`(a: DoubleArray, i: Int, j: Int): Boolean {
        return `bounded index`(a, i) and `bounded index`(a, j)
    }

    @Behavior
    fun `bounded index`(a: DoubleArray, i: Int, m: Int): Boolean {
        return `bounded index`(a, m)
    }

    @Behavior
    fun `is minimum`(a: DoubleArray, i: Int, j: Int, m: Int): Boolean {
        val k = Bindings.integer()
        return forall(k, implies(lte(i, k) and lt(k, j), gte(a[k], a[m])))
    }

    @Behavior
    fun `is minimum`(a: DoubleArray, i: Int, m: Int): Boolean {
        return `is minimum`(a, i, a.size, m)
    }

    @Behavior
    fun sorted(a: DoubleArray, i: Int, j: Int): Boolean {
        val k = Bindings.integer()
        val l = Bindings.integer()
        return forall(k, forall(l, implies(lte(i, k) and lt(k, l) and lt(l, j), lte(a[k], a[l]))))
    }

    @Behavior
    fun partitioned(a: DoubleArray, i: Int): Boolean {
        val k = Bindings.integer()
        val l = Bindings.integer()
        // forall k: int, l: int :: 0 <= k <= i < l <= a.length ==> a[k] <= a[l]
        return forall(k, forall(l, implies(lte(0, k) and lt(k, i) and lte(i, l) and lt(l, a.size), lte(a[k], a[l]))))
    }

    @Behavior
    fun `array is not empty`(a: DoubleArray): Boolean {
        return gt(a.size, 1)
    }

    @Behavior
    fun `array is sorted`(a: DoubleArray): Boolean {
        return sorted(a, 0, a.size)
    }

    @Behavior
    fun `array is not null`(a: DoubleArray): Boolean {
        return neq(a, null)
    }

    @TwoState
    @Behavior
    fun `swapped elements`(a: DoubleArray, i: Int, j: Int): Boolean {
        return eq(old(a[i]), a[j]) and eq(old(a[j]), a[i])
    }

    @TwoState
    @Behavior
    fun `elements invariance`(a: DoubleArray, i: Int, j: Int): Boolean {
        val k = Bindings.integer()
        return forall(k, implies(lte(0, k) and lt(k, a.size) and neq(k, i) and neq(k, j), eq(a[k], old(a[k]))))
    }

    @Return
    @Require("array is not null")
    @Require("array is not empty")
    @Ensure("array is sorted")
    fun sort(a: DoubleArray) {
        var i = 0
        while (i < a.size) {
            invariant(lte(0, i) and lte(i, a.size))
            invariant(sorted(a, 0, i))
            invariant(partitioned(a, i))

            var m = i
            var j = i
            while (j < a.size) {
                invariant(lte(i, j) and lte(j, a.size))
                invariant(lte(i, m) and lt(m, a.size))
                invariant(`is minimum`(a, i, j, m))

                if (a[j] < a[m]) {
                    m = j
                }
                j++
            }

            val y = a[i]
            a[i] = a[m]
            a[m] = y

            i++
        }
    }
}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
