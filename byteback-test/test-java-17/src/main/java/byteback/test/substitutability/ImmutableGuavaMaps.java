/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c byteback.test.substitutability.ImmutableGuavaMapSpec -c %{class}$ImmutableGuavaMapSpec -c %{ghost}CollectionSpec -c %{ghost}MapSpec -o %t.bpl
 */

package byteback.test.substitutability;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.Ghost.Attach;

public class ImmutableGuavaMaps<K, V> {

	@Attach("com.google.common.collect.ImmutableMap")
	public static class ImmutableGuavaMapSpec {

		@Abstract
		@Return
		public static <K, V> ImmutableMap<K, V> of() {
			throw new UnsupportedOperationException();
		}

		@Abstract
		@Return
		public static <K, V> ImmutableMap<K, V> of(K k1, V v1) {
			throw new UnsupportedOperationException();
		}

		@Abstract
		@Return
		public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2) {
			throw new UnsupportedOperationException();
		}

	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_UnmodifiableMap() {
		final ImmutableMap<Object, Object> m = ImmutableMap.of(new Object(), new Object());
		m.put(new Object(), new Object());
	}

	@Behavior
	public boolean m0_is_unmodifiable(final Map<Object, Object> m0) {
		return Ghost.of(CollectionSpec.class, m0).is_unmodifiable();
	}

	@Require("m0_is_unmodifiable")
	@Raise(exception = UnsupportedOperationException.class)
	public void Add_To_Unmodifiable_Map(final Map<Object, Object> m0) {
		m0.put(new Object(), new Object());
	}

	@Behavior
	public boolean returns_unmodifiable(final Map<Object, Object> r) {
		return Ghost.of(CollectionSpec.class, r).is_unmodifiable();
	}

	@Return
	@Ensure("returns_unmodifiable")
	public Map<Object, Object> Make_Unmodifiable_Map() {
		return ImmutableMap.of(new Object(), new Object());
	}

	@Return
	@Ensure("returns_unmodifiable")
	public Map<Object, Object> Make_Empty_Unmodifiable_Map() {
		return ImmutableMap.of();
	}

	@Raise(exception = UnsupportedOperationException.class)
	public void Indirect_Add_To_Unmodifiable_Map() {
		final Map<Object, Object> m0 = ImmutableMap.of();
		Add_To_Unmodifiable_Map(m0);
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 8 verified, 0 errors
 */
