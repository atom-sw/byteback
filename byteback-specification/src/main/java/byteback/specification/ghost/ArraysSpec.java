package byteback.specification.ghost;

import static byteback.specification.Operators.not;

import java.util.List;

import byteback.specification.Contract.Abstract;
import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Ensure;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost.Attach;

@Attach("java.util.Arrays")
public class ArraysSpec {

	@Behavior
	public static <T> boolean returns_irresizeable(T[] a, List<T> r) {
		return not(Ghost.of(CollectionSpec.class, r).is_resizeable());
	}

	@Behavior
	public static <T> boolean returns_modifiable(T[] a, List<T> r) {
		return Ghost.of(CollectionSpec.class, r).is_modifiable();
	}

	@Return
	@Ensure("returns_irresizeable")
	@Ensure("returns_modifiable")
	@Abstract
	public static <T> List<T> asList(T... a) {
		throw new UnsupportedOperationException();
	}

}
