package byteback.test.conversion;

import byteback.specification.Contract.*;
import byteback.specification.ghost.*;
import byteback.specification.ghost.Ghost
import byteback.specification.ghost.Ghost.*;

@Attach("kotlin.collections.CollectionsKt__CollectionsKt")
object CollectionsKtSpec {

		@Behavior
		@JvmStatic
		fun <T> `returns mutable`(c: java.util.Collection<T>, r: java.util.List<T>): Boolean {
				return Ghost.of(ListSpec::class.java, r).`is_mutable`();
		}

		@Return
		@Abstract
		@Ensure("returns mutable")
		@JvmStatic
		fun <T> toMutableList(c: java.util.Collection<T>): java.util.List<T> {
				throw UnsupportedOperationException();
		}

		@Behavior
		@JvmStatic
		fun <T> `returns immutable`(c: java.util.Collection<T>, r: java.util.List<T>): Boolean {
				return Ghost.of(ListSpec::class.java, r).`is_immutable`();
		}

		@Return
		@Abstract
		@Ensure("returns immutable")
		@JvmStatic
		fun <T> toList(c: java.util.Collection<T>): java.util.List<T> {
				throw UnsupportedOperationException();
		}

		@Behavior
		@JvmStatic
		fun <T> `returns mutable`(c: java.lang.Iterable<T>, r: java.util.Set<T>): Boolean {
				return Ghost.of(CollectionSpec::class.java, r).`is_mutable`();
		}

		@Return
		@Abstract
		@Ensure("returns mutable")
		@JvmStatic
		fun <T> toMutableSet(c: java.lang.Iterable<T>): java.util.Set<T> {
				throw UnsupportedOperationException();
		}

		@Behavior
		@JvmStatic
		fun <T> `returns immutable`(c: java.lang.Iterable<T>, r: java.util.Set<T>): Boolean {
				return Ghost.of(CollectionSpec::class.java, r).`is_immutable`();
		}

		@Return
		@Abstract
		@Ensure("returns immutable")
		@JvmStatic
		fun <T> toSet(c: java.lang.Iterable<T>): java.util.Set<T> {
				throw UnsupportedOperationException();
		}

}
