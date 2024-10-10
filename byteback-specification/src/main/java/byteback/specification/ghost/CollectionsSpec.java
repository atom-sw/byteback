package byteback.specification.ghost;

import static byteback.specification.Operators.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Return;
import byteback.specification.Contract.NoState;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.Collections")
public class CollectionsSpec {

	@NoState
	@Behavior
	public static <T> boolean returns_immutable(final List<?> l, final List<?> r) {
		return not(Ghost.of(CollectionSpec.class, l).is_mutable());
	}

	@Return
	@Ensure("returns_immutable")
	@Abstract
	public static <T> List<T> unmodifiableList(final List<? extends T> l) {
		throw new UnsupportedOperationException();
	}

	@NoState
	@Behavior
	public static <T> boolean returns_immutable(final Set<?> l, final Set<?> r) {
		return not(Ghost.of(CollectionSpec.class, l).is_mutable());
	}

	@Return
	@Ensure("returns_immutable")
	@Abstract
	public static <T> Set<T> unmodifiableSet(final Set<? extends T> l) {
		throw new UnsupportedOperationException();
	}

	@NoState
	@Behavior
	public static <T> boolean returns_immutable(final Map<?, ?> l, final Map<?, ?> r) {
		return not(Ghost.of(CollectionSpec.class, l).is_mutable());
	}

	@Return
	@Ensure("returns_immutable")
	@Abstract
	public static <K, V> Map<K, V> unmodifiableMap(final Map<? extends K, ? extends V> l) {
		throw new UnsupportedOperationException();
	}

}
