/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}ListSpec -c %{ghost}ArrayListSpec -c %{ghost}LinkedListSpec -c %{ghost}CollectionsSpec -c %{ghost}CollectionSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.ListSpec;
import byteback.specification.ghost.CollectionSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class UnmodifiableLists {

	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_UnmodifiableList() {
		final List<Object> l = Collections.unmodifiableList(new ArrayList<>());
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
		return Collections.unmodifiableList(new ArrayList<>());
	}

	@Return
	@Ensure("returns_unmodifiable")
	public List<Object> Make_Empty_Unmodifiable_List() {
		return Collections.unmodifiableList(new ArrayList<>());
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Indirect_Add_To_Unmodifiable_List() {
		final List<Object> l0 = Collections.unmodifiableList(new ArrayList<>());
		Add_To_Unmodifiable_List(l0);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 7 verified, 0 errors
 */
