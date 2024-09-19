/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$MapSpec -c %{class}$HashMapSpec -o %t.bpl
 */
package byteback.test.substitutability;

import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.Ghost.Attach;

import java.util.HashMap;
import java.util.Map;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Return;

public class UnmodifiableMaps {

	@Attach("java.util.Map")
	public static interface MapSpec<K, V> {

		@Implicit
		@Behavior
		boolean is_mutable();

		@Return(when = "is_mutable")
		void clear();

		@Return(when = "is_mutable")
		V put(K k, V v);

		@Return(when = "is_mutable")
		void putAll(Map<? extends K, ? extends V> m);

		@Return(when = "is_mutable")
		boolean remove(K k);

		@Return(when = "is_mutable")
		boolean remove(Object k, Object v);

	}

	@Attach("java.util.HashMap")
	@Invariant("is_mutable")
	public static class HashMapSpec<K, V> {

		@Behavior
		public boolean is_mutable() {
			return Ghost.of(MapSpec.class, this).is_mutable();
		}

		@Abstract
		@Return
		public HashMapSpec() {
		}

	}

	@Return
	public void main0() {
		final HashMap<Object, Object> m0 = new HashMap<>();
		m0.put(new Object(), new Object());
	}

	@Behavior
	public boolean m0_is_mutable(final Map<Object, Object> m0) {
		return Ghost.of(MapSpec.class, m0).is_mutable();
	}

	@Return(when = "m0_is_mutable")
	public void main1(final Map<Object, Object> m0) {
		m0.put(new Object(), new Object());
	}

	@Return
	public void main2() {
		final HashMap<Object, Object> m0 = new HashMap<>();
		main1(m0);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
