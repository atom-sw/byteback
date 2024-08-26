/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$ListSpec -c java.util.List -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.ghost.Ghost.Export;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Return;

public class UnmodifiableLists {

	@Attach("java.util.List")
	public static interface ListSpec<T> {

		@Behavior
		@Export
		boolean is_mutable();

		@Behavior
		@Export
		default boolean is_mutable(final T element) {
			return is_mutable();
		}

		@Return(when = "is_mutable")
		@Export
		void add(T element);

		@Return(when = "is_mutable")
		@Export
		void remove(T element);

	}

	public void main() {
	}

}
