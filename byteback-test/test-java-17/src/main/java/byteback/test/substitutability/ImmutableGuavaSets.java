/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c byteback.test.substitutability.ImmutableGuavaCollectionSpec -c %{class}$ImmutableGuavaSetSpec -c %{ghost}CollectionSpec -c %{ghost}SetSpec -o %t.bpl
 */

package byteback.test.substitutability;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.Ghost.Attach;

public class ImmutableGuavaSets<T> {

	@Attach("com.google.common.collect.ImmutableSet")
	public static class ImmutableGuavaSetSpec {

		@Abstract
		@Return
		public static <T> ImmutableSet<T> of() {
			throw new UnsupportedOperationException();
		}

		@Abstract
		@Return
		public static <T> ImmutableSet<T> of(T e1) {
			throw new UnsupportedOperationException();
		}

		@Abstract
		@Return
		public static <T> ImmutableSet<T> of(T e1, T e2) {
			throw new UnsupportedOperationException();
		}

	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_UnmodifiableSet() {
		final ImmutableSet<Object> l = ImmutableSet.of(new Object(), new Object());
		l.add(new Object());
	}

	@Behavior
	public boolean l0_is_unmodifiable(final Set<Object> l0) {
		return Ghost.of(CollectionSpec.class, l0).is_unmodifiable();
	}

	@Require("l0_is_unmodifiable")
	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_Unmodifiable_Set(final Set<Object> l0) {
		l0.add(new Object());
	}

	@Behavior
	public boolean returns_unmodifiable(final Set<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_unmodifiable();
	}

	@Return
	@Ensure("returns_unmodifiable")
	public Set<Object> Make_Unmodifiable_Set() {
		return ImmutableSet.of(new Object());
	}

	@Return
	@Ensure("returns_unmodifiable")
	public Set<Object> Make_Empty_Unmodifiable_Set() {
		return ImmutableSet.of();
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Indirect_Add_To_Unmodifiable_Set() {
		final Set<Object> l0 = ImmutableSet.of();
		Add_To_Unmodifiable_Set(l0);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
