/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import byteback.annotations.Contract.*
import byteback.annotations.Operator.*
import byteback.annotations.Special.*
import byteback.annotations.Quantifier.*
import byteback.annotations.Binding

class ArrayReverse {

		@Pure
		fun `reverse of`(a: IntArray?, b: IntArray?): Boolean {
				val i: Int = Binding.integer();

				return and(eq(a!!.size, b!!.size),
									 forall(i, implies(lte(0, i) and lt(i, a.size), eq(a[i], b[b.size - 1 - i]))))
		}

		@Pure
		@Predicate
		fun `array is not null`(a: IntArray?, i: Int, j: Int): Boolean {
				return neq(a, null);
		}

		@Predicate
		fun `swapped elements`(a: IntArray?, i: Int, j: Int): Boolean {
				return implies(neq(a!!, null), eq(old(a[i]), a[j]) and eq(old(a[j]), a[i]));
		}

		@Return(`when` = "array is not null")
		@Ensure("swapped elements")
		fun swap(a: IntArray, i: Int, j: Int) {
				val y: Int = a[i]
				a[i] = a[j];
				a[j] = y;
		}

		@Predicate
		fun `reverses the array`(a: IntArray?): Boolean {
				return implies(not(`array is null`(a)), `reverse of`(a, old(a)));
		}

		@Pure
		@Predicate
		fun `array is null`(a: IntArray?): Boolean {
				return eq(a, null);
		}

		@Raise(`exception` = IllegalArgumentException::class, `when` = "array is null")
		@Ensure("reverses the array")
		fun reverse(a: IntArray) {
				val l = a.size - 1
				var i = 0

				while (i < (l - i)) {
						val k: Int = Binding.integer();
						invariant(forall(k, implies(lte(0, k) and lt(k, i) or lt(l - i, k) and lte(k, l), eq(a[k], old(a[l - k])))))
						invariant(forall(k, implies(lte(i, k) and lt(k, l - i), eq(a[k], old(a[k])))))
						invariant(lte(0, i) and lte(i, (l + 1) / 2))
						swap(a, i, l - i)

						i = i + 1;
				}
		}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
