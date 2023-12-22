/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.exceptions;

import static byteback.annotations.Operator.*;
import static byteback.annotations.Contract.*;

public class Basic {

	public static class Exception4 extends Exception {
		@Return
		public Exception4() {}
	}

	public Exception tryCatchBlock() {
		try {
			throw new Exception();
		} catch (final Exception e) {
			return e;
		}
	}

	@Return
	public void neverThrows() throws Exception {
	}

	public void neverCatches() {
		try {
			neverThrows();
		} catch (Exception e) {
			assertion(false);
		}
	}

	@Predicate
	public boolean always_throws() {
		return true;
	}

	@Raise(exception = Exception.class, when = "always_throws")
	public void alwaysThrows() throws Exception {
		throw new Exception();
	}

	public void alwaysCatches() {
		try {
			alwaysThrows();
			assertion(false);
		} catch (Exception e) {
		}
	}

	@Raise(exception = Exception.class, when = "always_throws")
	public void callsAlwaysThrows() throws Exception {
		alwaysThrows();
	}

	@Predicate
	public boolean argument_is_even(final int n) {
		return eq(n % 2, 0);
	}

	@Raise(exception = Exception.class, when = "argument_is_even")
	public void throwsIfEven(int n) throws Exception {
		if (n % 2 == 0) {
			throw new Exception();
		}
	}

	public void catchesIfEven() {
		try {
			throwsIfEven(2);
			assertion(false);
		} catch (Exception e) {
		}
	}

	int f;

	@Pure
	@Predicate
	public boolean f_is_1() {
		return eq(f, 1);
	}

	@Pure
	@Predicate
	public boolean f_is_2() {
		return eq(f, 2);
	}

	@Pure
	@Predicate
	public boolean f_is_3() {
		return eq(f, 3);
	}

	@Pure
	@Predicate
	public boolean f_is_4() {
		return eq(f, 4);
	}

	@Pure
	@Predicate
	public boolean f_is_gt_4() {
		return gt(f, 4);
	}

	@Raise(exception = Exception1.class, when = "f_is_1")
	@Raise(exception = Exception2.class, when = "f_is_2")
	@Raise(exception = Exception3.class, when = "f_is_3")
	@Raise(exception = Exception4.class, when = "f_is_4")
	@Return(when = "f_is_gt_4")
	public void throwsMultiple() throws Exception {
		if (f == 1) {
			throw new Exception1();
		} else if (f == 2) {
			throw new Exception2();
		} else if (f == 3) {
			throw new Exception3();
		} else if (f == 4) {
			throw new Exception4();
		}
	}

	@Pure
	@Predicate
	public boolean f_divides_2() {
		return eq(f % 2, 0);
	}

	@Pure
	@Predicate
	public boolean f_divides_3() {
		return eq(f % 3, 0);
	}

	@Pure
	@Predicate
	public boolean f_doesnt_divide_2_but_divides_3() {
		return not(f_divides_2()) & f_divides_3();
	}

	@Raise(exception = Exception1.class, when = "f_is_3")
	@Raise(exception = Exception2.class, when = "f_divides_2")
	@Raise(exception = Exception1.class, when = "f_doesnt_divide_2_but_divides_3")
	public void throwsAlternatingExceptions() throws Exception {
		if (f == 3) {
			throw new Exception1();
		}

		if (f % 2 == 0) {
			throw new Exception2();
		}

		if (f % 3 == 0) {
			throw new Exception1();
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 15 verified, 0 errors
 */
