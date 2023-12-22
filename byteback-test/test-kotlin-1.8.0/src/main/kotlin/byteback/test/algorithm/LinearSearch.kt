/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import byteback.annotations.Contract.*
import byteback.annotations.Operator.*

class LinearSearch {

		@Predicate
		fun <T> `array is not null`(a: Array<T>, n: T, left: Int, right: Int): Boolean {
				return neq(a, null)
		}

		@Predicate
		fun <T> `array is null`(a: Array<T>, n: Int, left: Int, right: Int): Boolean {
				return eq(a, null)
		}

		@Predicate
		fun <T> `bounded indices`(a: Array<T>, n: T, left: Int, right: Int): Boolean {
				return lte(0, left) and lte(left, right) and lte(right, a.count())
		}

		@Predicate
		fun <T> `result is index`(a: Array<T>, n: T, left: Int, right: Int, returns: Int): Boolean {
				return implies(lte(0, returns), eq(a[returns], n));
		}

		@Require("array is not null")
		@Require("bounded indices")
		@Ensure("result is index")
		@Return
		fun <T> search(a: Array<T>, n: T, left: Int, right: Int): Int {
				var i = left

				while (i < right) {
						invariant(lte(left, i) and lte(i, right));

						if (a[i] === n) {
								return i
						}

						i++
				}

				return -1
		}
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
