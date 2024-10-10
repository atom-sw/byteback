/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}SetSpec -c %{ghost}HashSetSpec -c %{ghost}CollectionsSpec -c %{ghost}CollectionSpec -o %t.bpl
 */
package byteback.test.substitutability;

import static byteback.specification.Operators.*;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.CollectionSpec;

import java.util.Set;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class NonNullableSets {

	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_SetOf_Set() {
		final Set<Object> s1 = Set.of(new Object());
		s1.add(new Object());
	}

	@Behavior
	public boolean s0_is_nonnullable(final Set<Object> s0) {
		return Ghost.of(CollectionSpec.class, s0).is_nonnullable();
	}
    
	@Require("s0_is_nonnullable")
	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_NonNullable_Set(final Set<Object> s0) {
		s0.add(new Object());
	}

	@Behavior
	public boolean returns_nonnullable(final Set<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_nonnullable();
	}
    
	@Return
	@Ensure("returns_nonnullable")
	public Set<Object> Make_NonNullable_Set() {
		return Set.of(new Object());
	}
    
	@Return
	@Ensure("returns_nonnullable")
	public Set<Object> Make_Empty_NonNullable_Set() {
		return Set.of();
	}
    
	@Raise(exception = NullPointerException.class)
	public Set<Object> Make_NonNullable_Set_With_Null_Arg() {
		return Set.of(null, new Object());
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Indirect_Add_To_NonNullable_Set() {
		final Set<Object> s0 = Set.of(new Object());

		Add_To_NonNullable_Set(s0);
	}

	@Behavior
	public boolean i_is_even(final int i) {
		return eq(i % 2, 0);
	}

	@Require("i_is_even")
	@Return
	public void Add_To_NonNullable_Set_If_i_NotEven(int i) {
		final Set<Object> s0 = Set.of(new Object());

		if (i % 2 != 0) {
			Add_To_NonNullable_Set(s0);
		}
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 9 verified, 0 errors
 */
