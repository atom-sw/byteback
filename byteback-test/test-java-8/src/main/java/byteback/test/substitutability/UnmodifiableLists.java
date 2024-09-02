/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$ListSpec -c java.util.List -c java.util.ArrayList -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.ghost.Ghost.Export;

import static byteback.specification.Contract.assertion;

import java.util.ArrayList;
import java.util.List;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Operator;
import byteback.specification.Contract.Return;

public class UnmodifiableLists {

	@Attach("java.util.Collections")
	public static interface CollectionsSpec {

		@Export
		@Abstract
		public static <T> List<T> unmodifiableList() {
			throw new UnsupportedOperationException();
		}

	}

	@Attach("java.util.ArrayList")
	public static class ArrayListSpec<T> {

		@Behavior
		public boolean is_mutable() {
			return Ghost.of(ListSpec.class, this).is_mutable();
		}

		@Export
		@Abstract
		@Return
		@Ensure("is_mutable")
		public ArrayListSpec() {
		}

	}

	@Attach("java.util.List")
	public static interface ListSpec<T> {

		@Operator
		@Behavior
		boolean is_mutable();

		@Behavior
		default boolean is_mutable(final T element) {
			return is_mutable();
		}

		@Export
		boolean add(T element);

		@Export
		boolean remove(T element);

	}

	public void main() {
		final ArrayList<Object> l0 = new ArrayList<>();
		assertion(Ghost.of(ArrayListSpec.class, l0).is_mutable());
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
