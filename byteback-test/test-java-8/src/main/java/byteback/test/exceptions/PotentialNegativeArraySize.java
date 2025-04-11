/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --nas -o %t.bpl
 */
package byteback.test.exceptions;

import static byteback.specification.Contract.assertion;
import static byteback.specification.Operators.*;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class PotentialNegativeArraySize {

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

	@Raise(exception = NegativeArraySizeException.class, when = "n_is_negative")
	@Return(when = "n_is_positive")
	public void potentialNegativeArray(int n) {
		final int[] a = new int[n];
	}

	@Return
	public void safeArrayInstantiation(int n) {
		if (n >= 0) {
			final int[] a = new int[n];
		}
	}

	@Raise(exception = NegativeArraySizeException.class, when = "always")
	public void incorrectArrayInstantiation(int n) {
		final int[] a = new int[-1];
	}

	public void CatchNegativeArraySizeException_WrappingNegativeArrayInstantiation_AlwaysEntersCatch() {
		try {
			final int[] a = new int[-1];
		} catch (final NegativeArraySizeException exception) {
			return;
		}
		assertion(false);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
