/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}ListSpec -c %{ghost}ArraysSpec -c %{ghost}ArrayListSpec -c %{ghost}LinkedListSpec -c %{ghost}CollectionsSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Return;

public class ModifiableLists {

	@Return
	public void Add_To_ArrayList() {
		final ArrayList<Object> l0 = new ArrayList<>();
		l0.add(new Object());
	}

	@Behavior
	public boolean l0_is_mutable(final List<Object> l0) {
		return Ghost.of(CollectionSpec.class, l0).is_mutable();
	}

	@Return(when = "l0_is_mutable")
	public void Add_To_Mutable_List(final List<Object> l0) {
		l0.add(new Object());
	}

	@Return
	public void Indirect_Add_To_ArrayList() {
		final ArrayList<Object> l0 = new ArrayList<>();
		Add_To_Mutable_List(l0);
	}

	@Return
	public void Add_If_InstanceOf_ArrayList(final List<Object> l0) {
		if (l0 instanceof ArrayList) {
			final ArrayList<Object> l1 = (ArrayList<Object>) l0;
			l1.add(new Object());
		}
	}

	@Behavior
	public boolean returns_mutable(final int i, final List<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Ensure("returns_mutable")
	@Return
	public List<Object> Make_Either_Mutable_ArrayList_Or_LinkedList(int i) {
		final List<Object> l1;

		if (i % 2 == 0) {
			l1 = new ArrayList<Object>();
		} else {
			l1 = new LinkedList<Object>();
		}

		return l1;
	}

	@Behavior
	public boolean returns_mutable(final List<Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Return
	@Ensure("returns_mutable")
	public List<Object> Make_Mutable_List() {
		return new ArrayList<>();
	}

	@Return
	public void Indirect_Add_To_Mutable_List() {
		final List<Object> l1 = Make_Mutable_List();
		Add_To_Mutable_List(l1);
	}

	@Ensure("returns_mutable")
	@Return
	public List<Object> Indirect_Make_And_Return_Mutable_List() {
		final List<Object> l1 = Make_Either_Mutable_ArrayList_Or_LinkedList(2);

		return l1;
	}

	@Ensure("returns_mutable")
	@Return
	public List<Object> Indirect_Make_Add_And_Return_Mutable_List() {
		final List<Object> l1 = Make_Either_Mutable_ArrayList_Or_LinkedList(2);
		l1.add(new Object());

		return l1;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 12 verified, 0 errors
 */
