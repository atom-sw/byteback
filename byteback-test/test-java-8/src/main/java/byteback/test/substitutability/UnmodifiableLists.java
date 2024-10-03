/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionSpec -c %{ghost}ListSpec -c %{ghost}ArraysSpec -c %{ghost}ArrayListSpec -c %{ghost}LinkedListSpec -c %{ghost}CollectionsSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class UnmodifiableLists {

	@Return
	public void main1() {
		final ArrayList<Object> l0 = new ArrayList<>();
		l0.add(new Object());
	}

	@Behavior
	public boolean l0_is_mutable(final List<Object> l0) {
		return Ghost.of(CollectionSpec.class, l0).is_mutable();
	}

	@Return(when = "l0_is_mutable")
	public void main2(final List<Object> l0) {
		l0.add(new Object());
	}

	@Return
	public void main3() {
		final ArrayList<Object> l0 = new ArrayList<>();
		main2(l0);
	}

	@Return
	public void main4(final List<Object> l0) {
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
	public List<Object> main5(int i) {
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
	public List<Object> makeMutableList() {
		return new ArrayList<>();
	}

	@Return
	public void main6() {
		final List<Object> l1 = makeMutableList();
		main2(l1);
	}

	@Ensure("returns_mutable")
	@Return
	public List<Object> main7() {
		final List<Object> l1 = main5(2);

		return l1;
	}

	@Ensure("returns_mutable")
	@Return
	public List<Object> main8() {
		final List<Object> l1 = main5(2);
		l1.add(new Object());

		return l1;
	}

	public void main9() {
		final List<Integer> l1 = List.of(1, 2, 3);
		l1.add(4);
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void main10() {
		final List<Object> l = Collections.unmodifiableList(new ArrayList<>());
		l.add(new Object());
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 14 verified, 0 errors
 */
