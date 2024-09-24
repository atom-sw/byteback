package byteback.specification.ghost;

import java.util.Map;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.Map")
public interface MapSpec<K, V> {

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
