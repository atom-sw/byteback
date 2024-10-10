/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}ListSpec -c %{ghost}ArrayListSpec -c %{ghost}LinkedListSpec -c %{ghost}CollectionsSpec -c %{ghost}CollectionSpec -o %t.bpl
 */
package byteback.test.substitutability;

import static byteback.specification.Operators.*;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.CollectionSpec;

import java.util.List;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class NonNullableLists {

	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_ListOf_List() {
		final List<Object> l1 = List.of(new Object());
		l1.add(new Object());
	}

	@Behavior
	public boolean l0_is_nonnullable(final List<Object> l0) {
		return Ghost.of(CollectionSpec.class, l0).is_nonnullable();
	}
	
	@Require("l0_is_nonnullable")
	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_NonNullable_List(final List<Object> l0) {
		l0.add(new Object());
	}

	@Behavior
	public boolean returns_nonnullable(final List<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_nonnullable();
	}
	
	@Return
	@Ensure("returns_nonnullable")
	public List<Object> Make_NonNullable_List() {
		return List.of(new Object());
	}
	
	@Return
	@Ensure("returns_nonnullable")
	public List<Object> Make_Empty_NonNullable_List() {
		return List.of();
	}
	
	@Raise(exception = NullPointerException.class)
	public List<Object> Make_NonNullable_List_With_Null_Arg() {
		return List.of(null, new Object());
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Indirect_Add_To_NonNullable_List() {
		final List<Object> l0 = List.of(new Object());

		Add_To_NonNullable_List(l0);
	}

	@Behavior
	public boolean i_is_even(final int i) {
		return eq(i % 2, 0);
	}

	@Require("i_is_even")
	@Return
	public void Add_To_NonNullable_List_If_i_NotEven(int i) {
		final List<Object> l0 = List.of(new Object());

		if (i % 2 != 0) {
			Add_To_NonNullable_List(l0);
		}
	}

}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 9 verified, 0 errors
 */
