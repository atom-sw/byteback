package byteback.specification.ghost;

import java.util.Collection;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.List")
public interface ListSpec<T> {

	@Implicit
	@Behavior
	boolean is_mutable();

	@Return(when = "is_mutable")
	boolean add(T element);

	@Return(when = "is_mutable")
	boolean add(int index, T element);

	@Return(when = "is_mutable")
	boolean addAll(Collection<? extends T> c);

	@Return(when = "is_mutable")
	boolean addAll(int index, Collection<? extends T> c);

	@Return(when = "is_mutable")
	void clear();

	@Return(when = "is_mutable")
	T remove(int index);

	@Return(when = "is_mutable")
	boolean remove(final Object e);

	@Return(when = "is_mutable")
	boolean removeAll(Collection<?> c);

	@Return(when = "is_mutable")
	boolean retainAll(Collection<?> c);

	@Return(when = "is_mutable")
	T set(int index, T element);

}
