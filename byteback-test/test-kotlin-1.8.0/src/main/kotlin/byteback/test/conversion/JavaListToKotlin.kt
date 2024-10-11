/** 
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl	
 */

package byteback.test.conversion;

import byteback.specification.ghost.Ghost
import byteback.specification.ghost.*;
import byteback.specification.Contract.*;

class JavaListToKotlin {

		@Behavior
		fun <A> `l is mutable`(l: java.util.List<A>): Boolean {
				return Ghost.of(ListSpec::class.java, l).is_mutable()
		}

		@Require("l_is_mutable")
		@Return
		fun <T> `Convert JavaList To MutableList`(l: java.util.List<T>): MutableList<T> {
				return l.toMutableList()
		}

		fun <T> `Implicit Convert MutableList To JavaList`(l: java.util.List<T>): MutableList<T> {
				return l.toMutableList()
		}
		
}
