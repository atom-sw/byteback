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
public interface ListSpec<T> {

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

	@Behavior
	default boolean index_is_in_bounds(int index, T element) {
		return lte(0, index) & lt(index, Ghost.of(CollectionSpec.class, this).size());
	}

	@Behavior
	default boolean index_is_out_of_bounds(int index, T element) {
		return lt(index, 0) | lte(Ghost.of(CollectionSpec.class, this).size(), index);
	}

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Raise(exception = IndexOutOfBoundsException.class, when = "index_is_out_of_bounds")
	@Return(when = "index_is_in_bounds")
	T set(int index, T element);

	@Behavior
	default boolean index_is_in_bounds(int index) {
		return lte(0, index) & lt(index, Ghost.of(CollectionSpec.class, this).size());
	}

	@Behavior
	default boolean index_is_out_of_bounds(int index) {
		return lt(index, 0) | lte(Ghost.of(CollectionSpec.class, this).size(), index);
	}

	@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
	@Raise(exception = IndexOutOfBoundsException.class, when = "index_is_out_of_bounds")
	@Return(when = "index_is_in_bounds")
	T get(int index);

	@Behavior
	public static <E> boolean returns_immutable(List<E> l) {
		return Ghost.of(CollectionSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(List<E> l) {
		return Ghost.of(CollectionSpec.class, l).is_nonnullable();
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
		return Ghost.of(CollectionSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(E e1, List<E> l) {
		return Ghost.of(CollectionSpec.class, l).is_nonnullable();
	}

	@Behavior
	public static <E> boolean arguments_are_null(E e1) {
		return eq(e1, null);
	}

	@Behavior
	public static <E> boolean arguments_are_not_null(E e1) {
		return not(arguments_are_null(e1));
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
		return not(arguments_are_null(e1, e2));
	}

	@Behavior
	public static <E> boolean returns_immutable(E e1, E e2, List<E> l) {
		return Ghost.of(CollectionSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(E e1, E e2, List<E> l) {
		return Ghost.of(CollectionSpec.class, l).is_nonnullable();
	}

	@Abstract
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	@Return(when = "arguments_are_not_null")
	@Raise(exception = NullPointerException.class, when = "arguments_are_null")
	public static <E> List<E> of(E e1, E e2) {
		throw new UnsupportedOperationException();
	}

}
