/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$Resource -c java.lang.IllegalStateException --npe --iobe -o %t.bpl
 */
package byteback.test.examples;

import java.util.NoSuchElementException;

import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;
import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Special.*;

public class ReadResource {

	public static abstract class Resource implements AutoCloseable {

		public boolean isClosed;

		public boolean hasNext;

		@Return
		@Ensure("is_closed")
		public void close() {
			isClosed = true;
		}

		@Behavior
		public boolean is_closed() {
			return isClosed;
		}

		@Behavior
		public boolean is_open() {
			return not(isClosed);
		}

		@Behavior
		public boolean has_next() {
			return hasNext;
		}

		@Behavior
		public boolean has_no_next() {
			return not(hasNext);
		}

		@TwoState
		@Behavior
		public boolean open_invariant(int returns) {
			return eq(old(isClosed), isClosed);
		}

		@Behavior
		public boolean is_open_and_has_next() {
			return not(isClosed) & hasNext;
		}

		@Raise(exception = IllegalStateException.class, when = "is_closed")
		@Raise(exception = NoSuchElementException.class, when = "has_no_next")
		@Ensure("open_invariant")
		@Return(when = "is_open_and_has_next")
		public abstract int read();

	}

	@Behavior
	public static boolean a_is_null(Resource r, int[] a) {
		return eq(a, null);
	}

	@Behavior
	public static boolean r_is_null(Resource r, int[] a) {
		return eq(r, null);
	}

	@Behavior
	public static boolean r_and_a_are_not_null(Resource r, int[] a) {
		return neq(r, null) & neq(a, null);
	}

	@Behavior
	public static boolean r_or_a_is_null(Resource r, int[] a) {
		return neq(r, null) | neq(a, null);
	}

	@Behavior
	public static boolean r_is_open(Resource r, final int[] a) {
		return implies(neq(r, null), not(r.isClosed));
	}

	@Behavior
	public static boolean r_is_closed(final Resource r, final int[] a) {
		return implies(neq(r, null), r.isClosed);
	}

	@Behavior
	public static boolean a_is_empty(final Resource r, final int[] a, final int n) {
		return neq(a, null) & eq(a.length, 0);
	}

	@Require("r_is_open")
	@Ensure("r_is_closed")
	@Raise(exception = NullPointerException.class, when = "r_is_null")
	@Return(when = "r_and_a_are_not_null")
	public static void readInto(final Resource r, final int[] a) {
		try (r) {
			int i = 0;
			while (true) {
				invariant(lte(0, i) & lte(i, a.length + 1));
				invariant(implies(neq(r, null), r.is_open()));
				a[i] = r.read();
				i++;
			}
		} catch (IndexOutOfBoundsException | NoSuchElementException e) {
			return;
		}
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 8 verified
 */
