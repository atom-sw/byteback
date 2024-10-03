/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}MapSpec -c %{ghost}HashMapSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.MapSpec;

import java.util.HashMap;
import java.util.Map;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Return;

public class UnmodifiableMaps {

	@Return
	public void main1() {
		final HashMap<Object, Object> m0 = new HashMap<>();
		m0.put(new Object(), new Object());
	}

	@Behavior
	public boolean m0_is_mutable(final Map<Object, Object> m0) {
		return Ghost.of(MapSpec.class, m0).is_mutable();
	}

	@Return(when = "m0_is_mutable")
	public void main2(final Map<Object, Object> m0) {
		m0.put(new Object(), new Object());
	}

	@Return
	public void main3() {
		final HashMap<Object, Object> m0 = new HashMap<>();
		main2(m0);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
