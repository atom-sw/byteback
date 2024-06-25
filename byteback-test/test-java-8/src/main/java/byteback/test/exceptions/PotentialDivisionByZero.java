/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --dbz -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Return;
import byteback.specification.Contract.Returns;

import static byteback.specification.Operators.*;

@SuppressWarnings("unused")
public class PotentialDivisionByZero {

	@Behavior
	public boolean n_is_zero(int n) {
		return eq(n, 0);
	}

	@Behavior
	public boolean n_is_not_zero(int n) {
		return neq(n, 0);
	}

	@Behavior
	public boolean always() {
		return true;
	}

	@Return(when = "n_is_not_zero")
	@Raise(when = "n_is_zero", exception = ArithmeticException.class)
	public int potentialDivisionByZero(int n) {
		return 10 / n;
	}

	@Raise(exception = ArithmeticException.class, when = "always")
	public int divisionByZero() {
		return 10 / 0;
	}

	@Return
	public float validFloatDivision(float n) {
		return 10 / n;
	}

	@Return
	public double validDoubleDivision(double n) {
		return 10 / n;
	}

	@Return
	public int validDivisionByZero(int n) {
		if (n != 0) {
			return 10 / n;
		} else {
			return 0;
		}
	}

	@Return(when = "always")
	public int caughtDivisionByZero() {
		try {
			return 10 / 0;
		} catch (ArithmeticException exception) {
			return 0;
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
