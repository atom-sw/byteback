package byteback.specification.ghost;

import static byteback.specification.Operators.eq;
import static byteback.specification.Operators.not;

import java.util.Map;

import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.Contract.NoState;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Raise;

@Attach("java.util.Map")
public interface MapSpec<K, V> {

	@NoState
	@Implicit
	@Behavior
	default boolean is_mutable() {
		return Ghost.of(CollectionSpec.class, this).is_mutable();
	}

	@NoState
	@Implicit
	@Behavior
	default boolean is_immutable() {
		return not(is_mutable());
	}

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	void clear();

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	V put(K k, V v);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	void putAll(Map<? extends K, ? extends V> m);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean remove(K k);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean remove(Object k, Object v);

	@Behavior
	public static <K, V> boolean returns_immutable(Map<K, V> m) {
		return Ghost.of(CollectionSpec.class, m).is_immutable();
	}

	@Behavior
	public static <K, V> boolean returns_non_nullable(Map<K, V> m) {
		return not(Ghost.of(CollectionSpec.class, m).is_nullable());
	}

	@Abstract
	@Return
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	public static <K, V> Map<K, V> of() {
		throw new UnsupportedOperationException();
	}

	@Behavior
	public static <K, V> boolean returns_immutable(K k1, V v1, Map<K, V> m) {
		return Ghost.of(CollectionSpec.class, m).is_immutable();
	}

	@Behavior
	public static <K, V> boolean returns_non_nullable(K k1, V v1, Map<K, V> m) {
		return not(Ghost.of(CollectionSpec.class, m).is_nullable());
	}

	@Behavior
	public static <K, V> boolean arguments_are_null(K k1, V v1) {
		return eq(k1, null) | eq(v1, null);
	}

	@Behavior
	public static <K, V> boolean arguments_are_not_null(K k1, V v1) {
		return not(arguments_are_null(k1, v1));
	}

	@Abstract
	@Return(when = "arguments_are_not_null")
	@Raise(exception = NullPointerException.class, when = "arguments_are_null")
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	public static <K, V> Map<K, V> of(K k1, V v1) {
		throw new UnsupportedOperationException();
	}

	@Behavior
	public static <K, V> boolean arguments_are_null(K k1, V v1, K k2, V v2) {
		return eq(k1, null) | eq(v1, null) | eq(k2, null) | eq(v2, null);
	}

	@Behavior
	public static <K, V> boolean arguments_are_not_null(K k1, V v1, K k2, V v2) {
		return not(arguments_are_null(k1, v1, k2, v2));
	}

	@Behavior
	public static <K, V> boolean returns_immutable(K k1, V v1, K k2, V v2, Map<K, V> m) {
		return Ghost.of(CollectionSpec.class, m).is_immutable();
	}

	@Behavior
	public static <K, V> boolean returns_non_nullable(K k1, V v1, K k2, V v2, Map<K, V> m) {
		return not(Ghost.of(CollectionSpec.class, m).is_nullable());
	}

	@Abstract
	@Return(when = "arguments_are_not_null")
	@Raise(exception = NullPointerException.class, when = "arguments_are_null")
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
		throw new UnsupportedOperationException();
	}

}
