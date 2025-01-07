/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c byteback.test.substitutability.ImmutableGuavaCollectionSpec -c %{class}$ImmutableGuavaListSpec -c %{ghost}CollectionSpec -c %{ghost}ListSpec -o %t.bpl
 */

package byteback.test.substitutability;

import java.util.List;

import com.google.common.collect.ImmutableList;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.Ghost.Attach;

public class ImmutableGuavaLists<T> {

	@Attach("com.google.common.collect.ImmutableList")
	public static class ImmutableGuavaListSpec {

		@Abstract
		@Return
		public static <T> ImmutableList<T> of() {
			throw new UnsupportedOperationException();
		}

		@Abstract
		@Return
		public static <T> ImmutableList<T> of(T e1) {
			throw new UnsupportedOperationException();
		}

		@Abstract
		@Return
		public static <T> ImmutableList<T> of(T e1, T e2) {
			throw new UnsupportedOperationException();
		}

	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_UnmodifiableList() {
		final ImmutableList<Object> l = ImmutableList.of(new Object(), new Object());
		l.add(new Object());
	}

	@Behavior
	public boolean l0_is_unmodifiable(final List<Object> l0) {
		return Ghost.of(CollectionSpec.class, l0).is_unmodifiable();
	}

	@Require("l0_is_unmodifiable")
	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_Unmodifiable_List(final List<Object> l0) {
		l0.add(new Object());
	}

	@Behavior
	public boolean returns_unmodifiable(final List<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_unmodifiable();
	}

	@Return
	@Ensure("returns_unmodifiable")
	public List<Object> Make_Unmodifiable_List() {
		return ImmutableList.of(new Object());
	}

	@Return
	@Ensure("returns_unmodifiable")
	public List<Object> Make_Empty_Unmodifiable_List() {
		return ImmutableList.of();
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Indirect_Add_To_Unmodifiable_List() {
		final List<Object> l0 = ImmutableList.of();
		Add_To_Unmodifiable_List(l0);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
