package byteback.test.conversion;

import java.util.List;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.ListSpec;

public class JavaListConsumer {

	@Behavior
	public static <T> boolean l_is_mutable(final List<T> l) {
		return Ghost.of(ListSpec.class, l).is_mutable();
	}

	@Require("l_is_mutable")
	@Return
	public static <T> void Consume_MutableList(final List<T> l) {
	}

	@Behavior
	public static <T> boolean l_is_immutable(final List<T> l) {
		return Ghost.of(ListSpec.class, l).is_immutable();
	}

	@Require("l_is_immutable")
	@Return
	public static <T> void Consume_ImmutableList(final List<T> l) {
	}

}
