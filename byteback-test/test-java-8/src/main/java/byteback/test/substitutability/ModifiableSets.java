/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}SetSpec -c %{ghost}HashSetSpec -c %{ghost}LinkedHashSetSpec -c %{ghost}CollectionsSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Return;

public class ModifiableSets {

	@Return
	public void Add_To_HashSet() {
		final HashSet<Object> s0 = new HashSet<>();
		s0.add(new Object());
	}

	@Behavior
	public boolean s0_is_mutable(final Set<Object> s0) {
		return Ghost.of(CollectionSpec.class, s0).is_mutable();
	}

	@Return(when = "s0_is_mutable")
	public void Add_To_Mutable_Set(final Set<Object> s0) {
		s0.add(new Object());
	}

	@Return
	public void Indirect_Add_To_HashSet() {
		final HashSet<Object> s0 = new HashSet<>();
		Add_To_Mutable_Set(s0);
	}

	@Return
	public void Add_If_InstanceOf_HashSet(final Set<Object> s0) {
		if (s0 instanceof HashSet) {
			final HashSet<Object> s1 = (HashSet<Object>) s0;
			s1.add(new Object());
		}
	}

	@Behavior
	public boolean returns_mutable(final int i, final Set<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Ensure("returns_mutable")
	@Return
	public Set<Object> Make_Either_Mutable_HashSet_Or_LinkedHashSet(int i) {
		final Set<Object> s1;

		if (i % 2 == 0) {
			s1 = new HashSet<Object>();
		} else {
			s1 = new LinkedHashSet<Object>();
		}

		return s1;
	}

	@Behavior
	public boolean returns_mutable(final Set<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Return
	@Ensure("returns_mutable")
	public Set<Object> Make_Mutable_Set() {
		return new HashSet<>();
	}

	@Return
	public void Indirect_Add_To_Mutable_Set() {
		final Set<Object> s1 = Make_Mutable_Set();
		Add_To_Mutable_Set(s1);
	}

	@Ensure("returns_mutable")
	@Return
	public Set<Object> Indirect_Make_And_Return_Mutable_Set() {
		final Set<Object> s1 = Make_Either_Mutable_HashSet_Or_LinkedHashSet(2);

		return s1;
	}

	@Ensure("returns_mutable")
	@Return
	public Set<Object> Indirect_Make_Add_And_Return_Mutable_Set() {
		final Set<Object> s1 = Make_Either_Mutable_HashSet_Or_LinkedHashSet(2);
		s1.add(new Object());

		return s1;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 11 verified, 0 errors
 */
