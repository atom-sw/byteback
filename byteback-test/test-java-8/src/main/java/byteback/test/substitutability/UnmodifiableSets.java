/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}SetSpec -c %{ghost}HashSetSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.SetSpec;

import java.util.HashSet;
import java.util.Set;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Return;

public class UnmodifiableSets {

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
