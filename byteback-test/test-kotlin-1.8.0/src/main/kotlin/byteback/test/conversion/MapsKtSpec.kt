package byteback.test.conversion;

import byteback.specification.Contract.*;
import byteback.specification.ghost.*;
import byteback.specification.ghost.Ghost
import byteback.specification.ghost.Ghost.*;

@Attach("kotlin.collections.MapsKt__MapsKt")
object MapsKtSpec {

		@Behavior
		@JvmStatic
		fun <K, V> `returns mutable`(c: Map<K, V>, r: java.util.Map<K, V>): Boolean {
				return Ghost.of(CollectionSpec::class.java, r).`is_mutable`();
		}

		@Return
		@Abstract
		@Ensure("returns mutable")
		@JvmStatic
		fun <K, V> toMutableMap(c: java.util.Map<K, V>): java.util.Map<K, V> {
				throw UnsupportedOperationException();
		}

		@Behavior
		@JvmStatic
		fun <K, V> `returns immutable`(c: java.util.Map<K, V>, r: java.util.Map<K, V>): Boolean {
				return Ghost.of(CollectionSpec::class.java, r).`is_immutable`();
		}

		@Return
		@Abstract
		@Ensure("returns immutable")
		@JvmStatic
		fun <K, V> toMap(c: java.util.Map<K, V>): java.util.Map<K, V> {
				throw UnsupportedOperationException();
		}

}
