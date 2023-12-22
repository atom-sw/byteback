/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --act -o %t.bpl
 */

package byteback.test.exceptions

import byteback.annotations.Contract.*
import byteback.annotations.Operator.*


class PotentialIndexOutOfBounds {

		@Predicate
		fun `a is not null`(a: IntArray): Boolean {
				return neq(a, null)
		}

		@Require("a is not null")
		@Return
		fun passedTarget(a: IntArray) {
				if (a.count() > 0) {
						val a_0: Int = a[0]
				}
		}

		@Return
		fun passedIndex(i: Int) {
				val a: IntArray = IntArray(10);

				if (0 <= i && i < a.count()) {
						val a_i: Int = a[i]
				}
		}

		@Predicate
		fun `a is not empty`(a: IntArray): Boolean {
				return gt(a.count(), 0)
		}

		@Require("a is not null")
		@Require("a is not empty")
		@Return
		fun constrainedTarget(a: IntArray) {
				val a_0: Int = a[0]
		}

		@Require("a is not null")
		@Raise(`exception` = IndexOutOfBoundsException::class)
		fun expectedIndexOutOfBounds(a: IntArray) {
				val a_m1: Int = a[-1]
		}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
