/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}SetSpec -c %{ghost}HashSetSpec -c %{ghost}CollectionsSpec -c %{ghost}CollectionSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.SetSpec;
import byteback.specification.ghost.CollectionSpec;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class UnmodifiableSets {

	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_UnmodifiableSet() {
		final Set<Object> s = Collections.unmodifiableSet(new HashSet<>());
		s.add(new Object());
	}

	@Behavior
	public boolean s0_is_unmodifiable(final Set<Object> s0) {
		return Ghost.of(CollectionSpec.class, s0).is_unmodifiable();
	}

	@Require("s0_is_unmodifiable")
	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_Unmodifiable_Set(final Set<Object> s0) {
		s0.add(new Object());
	}

	@Behavior
	public boolean returns_unmodifiable(final Set<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_unmodifiable();
	}

	@Return
	@Ensure("returns_unmodifiable")
	public Set<Object> Make_Unmodifiable_Set() {
		return Collections.unmodifiableSet(new HashSet<>());
	}

	@Return
	@Ensure("returns_unmodifiable")
	public Set<Object> Make_Empty_Unmodifiable_Set() {
		return Collections.unmodifiableSet(new HashSet<>());
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Indirect_Add_To_Unmodifiable_Set() {
		final Set<Object> s0 = Collections.unmodifiableSet(new HashSet<>());
		Add_To_Unmodifiable_Set(s0);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
