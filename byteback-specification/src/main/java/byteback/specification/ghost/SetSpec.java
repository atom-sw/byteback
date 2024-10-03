package byteback.specification.ghost;

import static byteback.specification.Operators.not;
import static byteback.specification.Operators.eq;

import java.util.Collection;
import java.util.Set;

import byteback.specification.Contract.Return;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.NoState;
import byteback.specification.Contract.Implicit;
import byteback.specification.Contract.Behavior;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.Set")
public interface SetSpec<V> {

	@NoState
	@Implicit
	@Behavior
	default boolean is_mutable() {
		return Ghost.of(CollectionSpec.class, this).is_mutable();
	}

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

	@Behavior
	public static <E> boolean returns_immutable(Set<E> l) {
		return Ghost.of(CollectionSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(Set<E> l) {
		return not(Ghost.of(CollectionSpec.class, l).is_nullable());
	}

	@Abstract
	@Return
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	public static <E> Set<E> of() {
		throw new UnsupportedOperationException();
	}

	@Behavior
	public static <E> boolean returns_immutable(E e1, Set<E> l) {
		return Ghost.of(CollectionSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(E e1, Set<E> l) {
		return not(Ghost.of(CollectionSpec.class, l).is_nullable());
	}

	@Behavior
	public static <E> boolean arguments_are_null(E e1) {
		return eq(e1, null);
	}

	@Abstract
	@Return
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	@Raise(exception = NullPointerException.class, when = "arguments_are_null")
	public static <E> Set<E> of(E e1) {
		throw new UnsupportedOperationException();
	}

	@Behavior
	public static <E> boolean arguments_are_null(E e1, E e2) {
		return eq(e1, null) | eq(e2, null);
	}

	@Behavior
	public static <E> boolean returns_immutable(E e1, E e2, Set<E> l) {
		return Ghost.of(CollectionSpec.class, l).is_immutable();
	}

	@Behavior
	public static <E> boolean returns_non_nullable(E e1, E e2, Set<E> l) {
		return not(Ghost.of(CollectionSpec.class, l).is_nullable());
	}

	@Abstract
	@Return
	@Ensure("returns_immutable")
	@Ensure("returns_non_nullable")
	public static <E> Set<E> of(E e1, E e2) {
		throw new UnsupportedOperationException();
	}

}
