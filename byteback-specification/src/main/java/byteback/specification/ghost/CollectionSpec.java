package byteback.specification.ghost;

import java.util.Collection;

import static byteback.specification.Operators.*;
import static byteback.specification.Special.*;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.NoState;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

import static byteback.specification.Contract.*;

@Attach("java.util.Collection")
@Invariant("this_size_is_gte_0")
public interface CollectionSpec<T> {

	@NoState
	@Behavior
	default boolean this_size_is_gte_0() {
		return gt(size(), 0);
	}

	@NoState
	@Implicit
	@Behavior
	boolean is_modifiable();

	@NoState
	@Implicit
	@Behavior
	boolean is_resizeable();

	@NoState
	@Implicit
	@Behavior
	boolean is_nullable();

	@NoState
	@Implicit
	@Behavior
	default boolean is_mutable() {
		return is_modifiable() & is_resizeable() & is_nullable();
	}

	@NoState
	@Implicit
	@Behavior
	default boolean is_immutable() {
		return not(is_mutable());
	}

	@NoState
	@Implicit
	@Behavior
	default boolean is_nonnullable() {
		return not(is_nullable());
	}

	@NoState
	@Implicit
	@Behavior
	default boolean is_unmodifiable() {
		return not(is_modifiable());
	}

	@NoState
	@Behavior
	int size();

	@Ensure("adds_element")
	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean add(T element);

	@Implicit
	@TwoState
	@Exceptional
	@Behavior
	default boolean adds_element() {
		return implies(isVoid(thrown()), eq(size(), old(size() + 1)));
	}

	@Ensure("adds_element")
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

	@Implicit
	@TwoState
	@Exceptional
	@Behavior
	default boolean removes_element() {
		return implies(isVoid(thrown()), eq(size(), old(size() - 1)));
	}

	@Ensure("removes_element")
	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	T remove(int index);

	@Ensure("removes_element")
	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	boolean remove(final Object e);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean removeAll(Collection<?> c);

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Return(when = "is_mutable")
	boolean retainAll(Collection<?> c);

}
