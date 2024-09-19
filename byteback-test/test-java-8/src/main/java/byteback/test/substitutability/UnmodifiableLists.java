/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}ListSpec -c %{ghost}ArrayListSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.ListSpec;

import java.util.ArrayList;
import java.util.List;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Return;

public class UnmodifiableLists {

	@Return
	public void main0() {
		final ArrayList<Object> l0 = new ArrayList<>();
		l0.add(new Object());
	}

	@Behavior
	public boolean l0_is_mutable(final List<Object> l0) {
		return Ghost.of(ListSpec.class, l0).is_mutable();
	}

	@Return(when = "l0_is_mutable")
	public void main1(final List<Object> l0) {
		l0.add(new Object());
	}

	@Return
	public void main2() {
		final ArrayList<Object> l0 = new ArrayList<>();
		main1(l0);
	}

	@Return
	void main3(final List<Object> l0) {
		if (l0 instanceof ArrayList) {
			final ArrayList<Object> l1 = (ArrayList<Object>) l0;
			l1.add(new Object());
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 5 verified, 0 errors
 */
