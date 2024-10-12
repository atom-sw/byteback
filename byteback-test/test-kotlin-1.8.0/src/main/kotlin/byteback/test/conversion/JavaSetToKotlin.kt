/** 
 * RUN: %{byteback} -cp %{jar} -c %{class} -c byteback.test.conversion.CollectionsKtSpec -c byteback.test.conversion.JavaSetConsumer -c %{ghost}SetSpec -c %{ghost}CollectionSpec -o %t.bpl	
 */

package byteback.test.conversion;

import byteback.specification.ghost.*;
import byteback.specification.ghost.Ghost
import byteback.specification.ghost.Ghost.*;
import byteback.specification.Contract.*;

class JavaSetToKotlin {

		@Behavior
		fun <T> `s is mutable`(s: Set<T>): Boolean {
				return Ghost.of(SetSpec::class.java, s).`is_mutable`()
		}

		@Behavior
		fun <T> `returns mutable`(s: Set<T>, r: Set<T>): Boolean {
				return Ghost.of(SetSpec::class.java, r).`is_mutable`()
		}

		@Return
		@Ensure("returns mutable")
		fun <T> `Convert JavaSet To MutableSet`(s: Set<T>): MutableSet<T> {
				return s.toMutableSet()
		}

		@Behavior
		fun <T> `s is immutable`(s: Set<T>): Boolean {
				return Ghost.of(CollectionSpec::class.java, s).`is_immutable`()
		}

		@Require("s is immutable")
		@Return
		fun <T> `Passing Set To JavaConsumer`(s: Set<T>): Unit {
				JavaSetConsumer.`Consume_ImmutableSet`(s);
		}

		@Require("s is mutable")
		@Return
		fun <T> `Passing MutableSet To JavaConsumer`(s: MutableSet<T>): Unit {
				JavaSetConsumer.`Consume_MutableSet`(s);
		}

		@Require("s is mutable")
		@Return
		fun `Add To MutableSet`(s: MutableSet<Any>): Unit {
				s.add(Object())
				s.add(Object())
				s.add(Object())
		}

		@Require("s is mutable")
		@Return
		fun `Add To MutableSet In Loop`(s: MutableSet<Any>): Unit {
				var i: Int = 0;

				while (i < 10) {
						s.add(Object())
						++i;
				}
		}

		@Require("s is mutable")
		@Return
		fun `Indirect Add To MutableSet In Loop`(s: MutableSet<Any>): Unit {
				var i: Int = 0;

				while (i < 10) {
						`Add To MutableSet`(s)
						++i;
				}
		}
		
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 12 verified, 0 errors
 */
