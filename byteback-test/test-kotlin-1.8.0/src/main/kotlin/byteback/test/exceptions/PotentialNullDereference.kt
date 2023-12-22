/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --nct -o %t.bpl
 */

package byteback.test.exceptions

import byteback.annotations.Contract.*
import byteback.annotations.Operator.*

class A @Return constructor() {

		@Return
		fun method() {}

}

class PotentialNullDereference {

		@Return
		fun freshTarget() {
				val a: A = A()
				a.method()
		}

		@Return
		fun passedTarget(a: A?) {
				if (a != null) {
						a.method()
				}
		}

		@Return
		fun passedArrayTarget(a: Array<A>?) {
				if (a != null) {
						val a_l: Int = a.size;
				}
		}

		@Predicate
		fun `a is not null`(a: IntArray?): Boolean {
				return neq(a, null);
		}

		@Return(`when` = "a is not null")
		fun constrainedPassedArrayTarget(a: IntArray): Unit {
				val a_1: Int = a[1];
		}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
