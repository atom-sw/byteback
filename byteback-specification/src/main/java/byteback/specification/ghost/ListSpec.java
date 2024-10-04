package byteback.specification.ghost;

import static byteback.specification.Operators.*;
import static byteback.specification.Special.*;
import static byteback.specification.Contract.thrown;

import java.util.Collection;
import java.util.List;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Exceptional;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.Invariant;
import byteback.specification.Contract.NoState;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;
import byteback.specification.Contract.TwoState;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.List")
@Invariant("size_is_gte_0")
public interface ListSpec<T> {

	@Behavior
	int size();

	@Behavior
	default boolean size_is_gte_0() {
		return gt(size(), 0);
	}

	@NoState
	@Implicit
	@Behavior
	default boolean is_modifiable() {
		return Ghost.of(CollectionSpec.class, this).is_modifiable();
	}

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

	@Behavior
	default boolean index_is_in_bounds(int index, T element) {
		return lte(0, index) & lt(index, size());
	}

	@Behavior
	default boolean index_is_out_of_bounds(int index, T element) {
		return lt(index, 0) | lte(size(), index);
	}

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Raise(exception = IndexOutOfBoundsException.class, when = "index_is_out_of_bounds")
	@Return(when = "index_is_in_bounds")
	T set(int index, T element);

	@Behavior
	public static <E> boolean returns_immutable(List<E> l) {
		return Ghost.of(ListSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(List<E> l) {
		return not(Ghost.of(CollectionSpec.class, l).is_nullable());
	}

	@Abstract
	@Return
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	public static <E> List<E> of() {
		throw new UnsupportedOperationException();
	}

	@Behavior
	public static <E> boolean returns_immutable(E e1, List<E> l) {
		return Ghost.of(ListSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(E e1, List<E> l) {
		return not(Ghost.of(CollectionSpec.class, l).is_nullable());
	}

	@Behavior
	public static <E> boolean arguments_are_null(E e1) {
		return eq(e1, null);
	}

	@Behavior
	public static <E> boolean arguments_are_not_null(E e1) {
		return not(eq(e1, null));
	}

	@Abstract
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	@Return(when = "arguments_are_not_null")
	@Raise(exception = NullPointerException.class, when = "arguments_are_null")
	public static <E> List<E> of(E e1) {
		throw new UnsupportedOperationException();
	}

	@Behavior
	public static <E> boolean arguments_are_null(E e1, E e2) {
		return eq(e1, null) | eq(e2, null);
	}

	@Behavior
	public static <E> boolean arguments_are_not_null(E e1, E e2) {
		return not(eq(e1, null) | eq(e2, null));
	}

	@Behavior
	public static <E> boolean returns_immutable(E e1, E e2, List<E> l) {
		return Ghost.of(ListSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(E e1, E e2, List<E> l) {
		return not(Ghost.of(CollectionSpec.class, l).is_nullable());
	}

	@Abstract
	@Return(when = "arguments_are_not_null")
	@Raise(exception = NullPointerException.class, when = "arguments_are_null")
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	public static <E> List<E> of(E e1, E e2) {
		throw new UnsupportedOperationException();
	}

}
