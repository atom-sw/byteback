/** 
 * RUN: %{byteback} -cp %{jar} -c %{class} -c byteback.test.conversion.MapsKtSpec -c byteback.test.conversion.JavaMapConsumer -c %{ghost}MapSpec -c %{ghost}CollectionSpec -o %t.bpl	
 */

package byteback.test.conversion;

import byteback.specification.ghost.*;
import byteback.specification.ghost.Ghost
import byteback.specification.ghost.Ghost.*;
import byteback.specification.Contract.*;

class JavaMapToKotlin {

		@Behavior
		fun <K, V> `s is mutable`(s: Map<K, V>): Boolean {
				return Ghost.of(MapSpec::class.java, s).`is_mutable`()
		}

		@Behavior
		fun <K, V> `returns mutable`(s: Map<K, V>, r: Map<K, V>): Boolean {
				return Ghost.of(MapSpec::class.java, r).`is_mutable`()
		}

		@Return
		@Ensure("returns mutable")
		fun <K, V> `Convert JavaMap To MutableMap`(s: Map<K, V>): MutableMap<K, V> {
				return s.toMutableMap()
		}

		@Behavior
		fun <K, V> `s is immutable`(s: Map<K, V>): Boolean {
				return Ghost.of(CollectionSpec::class.java, s).`is_immutable`()
		}

		@Require("s is immutable")
		@Return
		fun <K, V> `Passing Map To JavaConsumer`(s: Map<K, V>): Unit {
				JavaMapConsumer.`Consume_ImmutableMap`(s);
		}

		@Require("s is mutable")
		@Return
		fun <K, V> `Passing MutableMap To JavaConsumer`(s: MutableMap<K, V>): Unit {
				JavaMapConsumer.`Consume_MutableMap`(s);
		}

		@Require("s is mutable")
		@Return
		fun `Add To MutableMap`(s: MutableMap<Any, Any>): Unit {
				s.put(Object(), Object())
				s.put(Object(), Object())
				s.put(Object(), Object())
		}

		@Require("s is mutable")
		@Return
		fun `Add To MutableMap In Loop`(s: MutableMap<Any, Any>): Unit {
				var i: Int = 0;

				while (i < 10) {
						s.put(Object(), Object())
						++i;
				}
		}

		@Require("s is mutable")
		@Return
		fun `Indirect Add To MutableMap In Loop`(s: MutableMap<Any, Any>): Unit {
				var i: Int = 0;

				while (i < 10) {
						`Add To MutableMap`(s)
						++i;
				}
		}
		
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 12 verified, 0 errors
 */
