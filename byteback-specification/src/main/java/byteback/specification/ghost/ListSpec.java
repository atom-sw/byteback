package byteback.specification.ghost;

import static byteback.specification.Operators.not;

import java.util.Collection;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.NoState;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.List")
public interface ListSpec<T> {

	@NoState
	@Implicit
	@Behavior
	boolean is_mutable();

	@NoState
	@Implicit
	@Behavior
	default boolean is_immutable() {
		return not(is_mutable());
	}

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean add(T element);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean add(int index, T element);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean addAll(Collection<? extends T> c);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean addAll(int index, Collection<? extends T> c);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	void clear();

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	T remove(int index);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean remove(final Object e);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean removeAll(Collection<?> c);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean retainAll(Collection<?> c);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	T set(int index, T element);

}
