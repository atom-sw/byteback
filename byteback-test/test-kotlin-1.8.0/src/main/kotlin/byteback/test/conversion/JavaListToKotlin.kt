/** 
 * RUN: %{byteback} -cp %{jar} -c %{class} -c byteback.test.conversion.CollectionsKtSpec -c byteback.test.conversion.JavaListConsumer -c %{ghost}ListSpec -c %{ghost}CollectionSpec -o %t.bpl	
 */

package byteback.test.conversion;

import byteback.specification.ghost.*;
import byteback.specification.ghost.Ghost
import byteback.specification.ghost.Ghost.*;
import byteback.specification.Contract.*;

class JavaListToKotlin {

		@Behavior
		fun <T> `l is mutable`(l: List<T>): Boolean {
				return Ghost.of(ListSpec::class.java, l).`is_mutable`()
		}

		@Behavior
		fun <T> `returns mutable`(l: List<T>, r: List<T>): Boolean {
				return Ghost.of(ListSpec::class.java, r).`is_mutable`()
		}

		@Return
		@Ensure("returns mutable")
		fun <T> `Convert JavaList To MutableList`(l: List<T>): MutableList<T> {
				return l.toMutableList()
		}

		@Behavior
		fun <T> `l is immutable`(l: List<T>): Boolean {
				return Ghost.of(ListSpec::class.java, l).`is_immutable`()
		}

		@Require("l is immutable")
		@Return
		fun <T> `Passing List To JavaConsumer`(l: List<T>): Unit {
				JavaListConsumer.`Consume_ImmutableList`(l);
		}

		@Require("l is mutable")
		@Return
		fun <T> `Passing MutableList To JavaConsumer`(l: MutableList<T>): Unit {
				JavaListConsumer.`Consume_MutableList`(l);
		}

		@Require("l is mutable")
		@Return
		fun `Add To MutableList`(l: MutableList<Any>): Unit {
				l.add(Object())
				l.add(Object())
				l.add(Object())
		}

		@Require("l is mutable")
		@Return
		fun `Add To MutableList In Loop`(l: MutableList<Any>): Unit {
				var i: Int = 0;

				while (i < 10) {
						l.add(Object())
						++i;
				}
		}

		@Require("l is mutable")
		@Return
		fun `Indirect Add To MutableList In Loop`(l: MutableList<Any>): Unit {
				var i: Int = 0;

				while (i < 10) {
						`Add To MutableList`(l)
						++i;
				}
		}
		
}

/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 12 verified, 0 errors
 */
