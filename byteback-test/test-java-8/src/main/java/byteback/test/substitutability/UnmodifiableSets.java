/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$SetSpec -c %{class}$HashSetSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.Ghost.Attach;

import java.util.HashSet;
import java.util.Set;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Operator;
import byteback.specification.Contract.Return;

public class UnmodifiableSets {

	@Attach("java.util.Set")
	public static interface SetSpec<V> {

		@Operator
		@Behavior
		boolean is_mutable();

		@Behavior
		default boolean is_mutable(final V value) {
			return is_mutable();
		}

		@Return(when = "is_mutable")
		boolean add(V value);

		@Return(when = "is_mutable")
		boolean remove(V value);

	}

	@Attach("java.util.HashSet")
	@Invariant("is_mutable")
	public static class HashSetSpec<K, V> {

		@Behavior
		public boolean is_mutable() {
			return Ghost.of(SetSpec.class, this).is_mutable();
		}

		@Abstract
		@Return
		@Ensure("is_mutable")
		public HashSetSpec() {
		}

	}

	@Return
	public void main0() {
		final HashSet<Object> m0 = new HashSet<>();
		m0.add(new Object());
	}

	@Behavior
	public boolean m0_is_mutable(final Set<Object> m0) {
		return Ghost.of(SetSpec.class, m0).is_mutable();
	}

	@Return(when = "m0_is_mutable")
	public void main1(final Set<Object> m0) {
		m0.add(new Object());
	}

	@Return
	public void main2() {
		final HashSet<Object> m0 = new HashSet<>();
		main1(m0);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
