/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.exceptions;

import static byteback.annotations.Operator.*;
import static byteback.annotations.Contract.*;

public class TryFinally {

	@Predicate
	public boolean always() {
		return true;
	}

	@Raise(exception = Exception1.class, when = "always")
	public void alwaysThrows1() throws Exception1, Exception2 {
		throw new Exception1();
	}

	@Return
	public void finallyBlock() {
		try {
		} finally {
		}
	}

	@Return
	public void catchFinallyBlock() {
		try {
		} catch (Exception e) {
		}	finally {
		}
	}

	@Raise(exception = Exception1.class, when = "always")
	public void finallyIsExecuted() throws Exception {
		try {
			alwaysThrows1();
			assertion(false);
		} finally {
		}
		assertion(false);
	}

	public void finallyIsExecutedAfterThrowInCatch() throws Exception {
		try {
			alwaysThrows1();
			assertion(false);
		} catch (Exception1 e) {
			alwaysThrows1();
			assertion(false);
		} finally {
		}

		assertion(false);
	}

	public void unreachableCatch() throws Exception {
		try {
			alwaysThrows1();
			assertion(false);
		} catch (Exception2 e)  {
			assertion(false);
		}
	}

	@Predicate
	public boolean returns_2(int returns) {
		return eq(returns, 2);
	}

	@Ensure("returns_2")
	@SuppressWarnings("finally")
	public int finallyOverridesReturn() {
		try {
			return 1;
		} finally {
			return 2;
		}
	}

	@Ensure("returns_2")
	@SuppressWarnings("finally")
	public int finallyOverridesThrows() throws Exception {
		try {
			throw new Exception1();
		} finally {
			return 2;
		}
	}

	@Ensure("returns_2")
	@SuppressWarnings("finally")
	public int finallyOverrides1NestedFinally() throws Exception {
		try {
			try {
				throw new Exception1();
			} finally {
				return 1;
			}
		} finally {
			return 2;
		}
	}

	@Ensure("returns_2")
	@SuppressWarnings("finally")
	public int finallyOverrides2NestedFinally() throws Exception {
		try {
			try {
				try {
					throw new Exception1();
				} finally {
					return 3;
				}
			} finally {
				return 1;
			}
		} finally {
			return 2;
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 13 verified, 0 errors
 */
