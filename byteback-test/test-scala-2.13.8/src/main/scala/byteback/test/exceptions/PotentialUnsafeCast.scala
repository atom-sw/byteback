/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$A -c %{class}$B --cce -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract.*;
import byteback.specification.Operators.*;

class PotentialUnsafeCast {

  class A ()

	class B () extends A

	class C ()

	@Behavior
	def x_is_instanceof_A(x: Object): Boolean = {
		return x.instanceOf[A]
	}

	@Return(when = "x_is_instanceof_A")
	def unsafeDownCast(x: Object): Unit = {
		val a: A = x.asInstanceOf[A]
	}

	@Return
	def safeDownCast(x: Object): Unit = {
		if (x is A) {
			val a: A = x.asInstanceOf[A]
		}
	}

	@Return
	def safeUpcast(b: B): Unit = {
		val a: A = b as A
	}

	@Behavior
	def always(): Boolean = {
		return true
	}

	@Raise(exception = ClassCastException::class, when = "always")
	def invalidDownCast(): Unit = {
		val a: A = new A()
		val b: B = b.asInstanceOf[A]
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
