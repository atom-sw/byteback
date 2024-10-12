package byteback.test.conversion;

import java.util.Map;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Require;
import byteback.specification.Contract.Return;
import byteback.specification.ghost.CollectionSpec;
import byteback.specification.ghost.Ghost;
import byteback.specification.ghost.MapSpec;

public class JavaMapConsumer {

	@Behavior
	public static <K, V> boolean m_is_mutable(final Map<K, V> m) {
		return Ghost.of(MapSpec.class, m).is_mutable();
	}

	@Require("m_is_mutable")
	@Return
	public static <K, V> void Consume_MutableMap(final Map<K, V> m) {
	}

	@Behavior
	public static <K, V> boolean m_is_immutable(final Map<K, V> m) {
		return Ghost.of(CollectionSpec.class, m).is_immutable();
	}

	@Require("m_is_immutable")
	@Return
	public static <K, V> void Consume_ImmutableMap(final Map<K, V> m) {
	}

}
