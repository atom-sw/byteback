/**
 * RUN: %{byteback} -cp %{jar} -c %{class}$Sequence -c %{class}$Snapshot -c %{class}$List -o %t.bpl
 */
package byteback.test.generics;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Exceptional;
import byteback.specification.Contract.Return;
import byteback.specification.Contract.Raise;
import static byteback.specification.Special.*;
import static byteback.specification.Contract.*;

public abstract class Sequences {

	public abstract static class Sequence<T> {

		@Exceptional
		@Behavior
		public boolean throws_or_returns(final T e) {
			return isVoid(thrown()) | thrown() instanceof UnsupportedOperationException;
		}

		@Ensure("throws_or_returns")
		public abstract void extend(final T e);

	}

	static class Snapshot<T> extends Sequence<T> {

		@Raise
		public void extend(final T e) {
			throw new UnsupportedOperationException();
		}

	}

	static class List<T> extends Sequence<T> {

		@Override
		@Return
		public void extend(final T e)
		{ /* ... */ }

	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
