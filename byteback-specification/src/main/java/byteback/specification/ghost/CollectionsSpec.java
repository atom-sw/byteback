package byteback.specification.ghost;

import java.util.List;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.NoState;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.Collections")
public class CollectionsSpec {

	@NoState
	@Behavior
	public static <T> boolean returns_immutable(final List<?> l, final List<?> r) {
		return Ghost.of(ListSpec.class, l).is_immutable();
	}

	@Ensure("returns_immutable")
	@Abstract
	public static <T> List<T> unmodifiableList(final List<? extends T> l) {
		throw new UnsupportedOperationException();
	}

}
