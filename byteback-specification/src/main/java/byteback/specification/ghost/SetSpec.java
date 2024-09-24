package byteback.specification.ghost;

import java.util.Collection;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.Set")
public interface SetSpec<V> {

	@Implicit
	@Behavior
	boolean is_mutable();

	@Return(when = "is_mutable")
	boolean add(V e);

	@Return(when = "is_mutable")
	boolean addAll(final Collection<V> c);

	@Return(when = "is_mutable")
	boolean clear();

	@Return(when = "is_mutable")
	boolean remove(V e);

	@Return(when = "is_mutable")
	boolean removeAll(Collection<V> c);

	@Return(when = "is_mutable")
	boolean retainAll(Collection<V> c);

}
