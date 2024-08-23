/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$ListSpec -c java.util.List -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.ghost.Ghost.Export;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;

public class UnmodifiableLists {

	@Attach("java.util.List")
	public interface ListSpec<T> {

		@Export
		@Behavior
		boolean is_modifiable();

		@Raise(exception = UnsupportedOperationException.class,
					 when = "is_unmodifiable")
		@Export
		void add(T element);

		@Raise(exception = UnsupportedOperationException.class,
					 when = "is_unmodifiable")
		@Export
		void remove(T element);

	}

	public void main() {
	}

}
