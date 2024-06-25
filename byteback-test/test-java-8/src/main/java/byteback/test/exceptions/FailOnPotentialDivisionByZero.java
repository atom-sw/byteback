/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --dbz --strict -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Return;

import static byteback.specification.Operators.*;

public class FailOnPotentialDivisionByZero {

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

	public int potentialDivisionByZero(int n) {
		// CHECK: Error: this assertion could not be proved
		return 10 / n;
	}

	public int divisionByZero() {
		// CHECK: Error: this assertion could not be proved
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

	public int caughtDivisionByZero() {
		try {
			// CHECK: Error: this assertion could not be proved
			return 10 / 0;
		} catch (ArithmeticException exception) {
			return 0;
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 3 errors
 */
