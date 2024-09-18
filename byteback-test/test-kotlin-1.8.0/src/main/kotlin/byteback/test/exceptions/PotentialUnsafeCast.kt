/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$A -c %{class}$B --cce -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract.*;
import byteback.specification.Operators.*;

@SuppressWarnings("unused")
class PotentialUnsafeCast {

		open class A {
				@Return
				constructor()
		}

		class B : A() {
		}

		class C {
		}

		@Behavior
		fun `x is instanceof A`(x: Any): Boolean {
				return x is A
		}

		@Return(`when` = "x is instanceof A")
		fun unsafeDownCast(x: Any): Unit {
				var a: A = x as A
		}

		@Return
		fun safeDownCast(x: Any): Unit {
				if (x is A) {
						var a: A = x as A
				}
		}

		@Return
		fun safeUpcast(b: B): Unit {
				var a: A = b as A
		}

		@Behavior
		fun always(): Boolean {
				return true
		}

		@Raise(`exception` = ClassCastException::class, `when` = "always")
		fun invalidDownCast(): Unit {
				var a: A = A()
				var b: B = a as B
		}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
