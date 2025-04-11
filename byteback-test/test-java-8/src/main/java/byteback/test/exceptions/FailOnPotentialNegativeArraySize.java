/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --nas --strict -o %t.bpl
 */
package byteback.test.exceptions;

import static byteback.specification.Operators.*;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Return;

public class FailOnPotentialNegativeArraySize {

	@Behavior
	public boolean always(int n) {
		return true;
	}

	@Behavior
	public boolean n_is_negative(int n) {
		return lt(n, 0);
	}

	@Behavior
	public boolean n_is_positive(int n) {
		return gte(n, 0);
	}

	@Return(when = "n_is_positive")
	public void potentialNegativeArray(int n) {
		// CHECK: Error: this assertion could not be proved
		final int[] a = new int[n];
	}

	@Return
	public void safeArrayInstantiation(int n) {
		if (n >= 0) {
			final int[] a = new int[n];
		}
	}

	public void incorrectArrayInstantiation(int n) {
		// CHECK: Error: this assertion could not be proved
		final int[] a = new int[-1];
	}

	@Return
	public void CatchNegativeArraySizeException_WrappingNegativeArrayInstantiation_AlwaysEntersCatch() {
		try {
			// CHECK: Error: this assertion could not be proved
			final int[] a = new int[-1];
		} catch (final NegativeArraySizeException exception) {
			return;
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 3 errors
 */
