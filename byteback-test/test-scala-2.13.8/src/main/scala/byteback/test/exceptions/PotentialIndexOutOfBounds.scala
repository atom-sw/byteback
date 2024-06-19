/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --iobe -o %t.bpl
 */

package byteback.test.exceptions

import byteback.specification.Contract._;
import byteback.specification.Special._;
import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};


class PotentialIndexOutOfBounds {

		@Behavior
		def a_is_not_null(a: Array[Int]): Boolean = {
				return neq(a, null)
		}

		@Return
		def passedTarget(a: Array[Int]): Unit = {
				if (a.length > 0) {
						val a_0: Int = a(0)
				}
		}

		@Return
		def passedIndex(i: Int): Unit = {
				val a: Array[Int] = new Array[Int](10);

				if (0 <= i && i < a.length) {
						val a_i: Int = a(i)
				}
		}

		@Behavior
		def a_is_not_empty(a: Array[Int]): Boolean = {
				return gt(a.length, 0)
		}

		@Require("a_is_not_empty")
		@Return
		def constrainedTarget(a: Array[Int]): Unit = {
				val a_0: Int = a(0)
		}

		@Raise(exception = classOf[IndexOutOfBoundsException])
		def expectedIndexOutOfBounds(a: Array[Int]): Unit = {
				val a_m1: Int = a(-1)
		}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
