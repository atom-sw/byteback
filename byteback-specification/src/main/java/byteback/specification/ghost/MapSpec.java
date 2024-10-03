package byteback.specification.ghost;

import static byteback.specification.Operators.not;

import java.util.Map;

import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.Contract.NoState;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.Behavior;
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

}
