/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --cce -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract._;
import byteback.specification.Operators._;
import byteback.specification.Operators.{eq => equal};

class B () extends A ()

class C ()

class PotentialUnsafeCast {

	@Behavior
	def x_is_instanceof_A(x: Object): Boolean = {
		return x.isInstanceOf[A]
	}

	@Return(when = "x_is_instanceof_A")
	def unsafeDownCast(x: Object): Unit = {
		val a: A = x.asInstanceOf[A]
	}

	@Return
	def safeDownCast(x: Object): Unit = {
		if (x.isInstanceOf[A]) {
			val a: A = x.asInstanceOf[A]
		}
	}

	@Return
	def safeUpcast(b: B): Unit = {
		val a: A = b.asInstanceOf[A]
	}

	@Behavior
	def always(): Boolean = {
		return true
	}

	@Raise(exception = classOf[ClassCastException], when = "always")
	def invalidDownCast(): Unit = {
		val a: A = new A
		val b: B = a.asInstanceOf[B]
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
