/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.annotations.Contract.Predicate;
import byteback.annotations.Contract.Raise;
import byteback.annotations.Contract.Return;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;

public class Loop {

	@Predicate
	public boolean always() {
		return true;
	}

	@Raise(exception = Exception1.class, when = "always")
	public void alwaysThrows1() throws Exception1, Exception2 {
		throw new Exception1();
	}

	@Raise(exception = Exception.class, when = "always")
	public void throwsInFor() throws Exception {
		for (int i = 0; i < 10; ++i) {
			invariant(lte(0, i) & lt(i, 10));
			if (i > 5) {
				throw new Exception();
			}
		}
	}

	@Return
	public void doesNotThrowInFor() throws Exception {
		for (int i = 0; i < 10; ++i) {
			if (i >= 10) {
				throw new Exception();
			}
		}
	}

	@Return
	public void catchOutOfWhile() throws Exception {
		try {
			while (true) {
				throwsInFor();
				assertion(false);
			}
		} catch (Exception e) {
		}
	}

	@Return
	public void catchInWhile() {
		boolean iterate = true;

		while (iterate) {
			try {
				throwsInFor();
				assertion(false);
			} catch (Exception e) {
				iterate = false;
			}
		}
	}

	@Raise(exception = Exception1.class, when = "always")
	public void unreachableCatchInWhile() throws Exception {
		while (true) {
			try {
				alwaysThrows1();
				assertion(false);
			} catch (Exception2 e) {
				assertion(false);
			}
		}
	}

	@Raise(exception = Exception.class, when = "always")
	public void throwsInNestedFor() throws Exception {
		for (int i = 0; i < 10; ++i) {
			invariant(lte(0, i) & lt(i, 10));

			for (int j = 0; j < 10; ++j) {
				invariant(lte(0, j) & lt(j, 10));

				if (j > 5) {
					throw new Exception();
				}
			}
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 10 verified, 0 errors
 */
