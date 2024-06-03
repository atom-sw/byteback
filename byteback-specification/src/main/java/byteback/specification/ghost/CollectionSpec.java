package byteback.specification.ghost;

import byteback.specification.ghost.Ghost.Attach;
import byteback.specification.ghost.Ghost.Export;
import byteback.specification.Contract.Return;

import java.util.List;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;

import static byteback.specification.Operators.eq;

@Attach("java.util.Collection")
public abstract class CollectionSpec {

	@Export
	@Abstract
	public CollectionSpec() {
	}

	@Export
	@Return
	@Abstract
	abstract Object[] toArray();

	@Behavior
	public static <T> boolean returns_unmodifiable(List<T> list, List<T> returned) {
		return eq(Ghost.of(ListSpec.class, returned).is_unmodifiable(), true);
	}

	@Export
	@Ensure("returns_unmodifiable")
	@Abstract
	public static <T> List<T> unmodifiableList(List<T> list) {
		throw new UnsupportedOperationException();
	}

}
