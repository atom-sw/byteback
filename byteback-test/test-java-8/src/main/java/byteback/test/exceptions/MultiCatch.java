/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.exceptions;

import static byteback.specification.Contract.*;

public class MultiCatch {

	@Exceptional
	@Behavior
	public boolean always_throws_exception1_exception2() {
		return thrown() instanceof Exception1;
	}

	@Ensure("always_throws_exception1_exception2")
	public void alwaysThrowsMultiple() throws Exception {
		throw new Exception1();
	}

	public void emptyMulticatch() throws Exception {
		try {
			alwaysThrowsMultiple();
		} catch (Exception1 | Exception2 e) {
		}
	}

	public void multiCatchUnion() throws Exception {
		try {
			alwaysThrowsMultiple();
			assertion(false);
		} catch (Exception1 | Exception2 e) {
			assertion(e instanceof Exception1 | e instanceof Exception2);
		}
	}

	public void multiCatchFinally() throws Exception {
		try {
			alwaysThrowsMultiple();
			assertion(false);
		} catch (Exception3 | Exception4 e) {
			assertion(false);
		} finally {
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified
 */
