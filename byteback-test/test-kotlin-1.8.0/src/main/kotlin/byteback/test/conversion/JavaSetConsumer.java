package byteback.test.conversion;

import java.util.Set;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.SetSpec;

public class JavaSetConsumer {

	@Behavior
	public static <T> boolean l_is_mutable(final Set<T> l) {
		return Ghost.of(SetSpec.class, l).is_mutable();
	}

	@Require("l_is_mutable")
	@Return
	public static <T> void Consume_MutableSet(final Set<T> l) {
	}

	@Behavior
	public static <T> boolean l_is_immutable(final Set<T> l) {
		return Ghost.of(CollectionSpec.class, l).is_immutable();
	}

	@Require("l_is_immutable")
	@Return
	public static <T> void Consume_ImmutableSet(final Set<T> l) {
	}

}
