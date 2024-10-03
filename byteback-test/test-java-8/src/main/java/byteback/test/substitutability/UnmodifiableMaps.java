/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{ghost}CollectionsSpec -c %{ghost}MapSpec -c %{ghost}HashMapSpec -c %{ghost}LinkedHashMapSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class UnmodifiableMaps {

	@Return
	public void main1() {
		final HashMap<Object, Object> m0 = new HashMap<>();
		m0.put(new Object(), new Object());
	}

	@Behavior
	public boolean m0_is_mutable(final Map<Object, Object> m0) {
		return Ghost.of(CollectionSpec.class, m0).is_mutable();
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

	@Return
	public void main4(final Map<Object, Object> m0) {
		if (m0 instanceof HashMap) {
			final HashMap<Object, Object> m1 = (HashMap<Object, Object>) m0;
			m1.put(new Object(), new Object());
		}
	}

	@Behavior
	public boolean returns_mutable(final int i, final Map<Object, Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Ensure("returns_mutable")
	@Return
	public Map<Object, Object> main5(int i) {
		final Map<Object, Object> m1;

		if (i % 2 == 0) {
			m1 = new HashMap<Object, Object>();
		} else {
			m1 = new LinkedHashMap<Object, Object>();
		}

		return m1;
	}

	@Behavior
	public boolean returns_mutable(final Map<Object, Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_mutable();
	}

	@Return
	@Ensure("returns_mutable")
	public Map<Object, Object> makeMutableMap() {
		return new HashMap<>();
	}

	@Return
	public void main6() {
		final Map<Object, Object> m1 = makeMutableMap();
		main2(m1);
	}

	@Ensure("returns_mutable")
	@Return
	public Map<Object, Object> main7() {
		final Map<Object, Object> m1 = main5(2);

		return m1;
	}

	@Ensure("returns_mutable")
	@Return
	public Map<Object, Object> main8() {
		final Map<Object, Object> m1 = main5(2);
		m1.put(new Object(), new Object());

		return m1;
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void main10() {
		final Map<Object, Object> m = Collections.unmodifiableMap(new HashMap<>());
		m.put(new Object(), new Object());
	}
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 12 verified, 0 errors
 */
