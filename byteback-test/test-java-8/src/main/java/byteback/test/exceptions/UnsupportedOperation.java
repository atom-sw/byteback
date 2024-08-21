/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -c %{class}$Map -c %{class}$ImmutableMap -c %{class}$MutableMap -o %t.bpl
 */

package byteback.test.exceptions;

import static byteback.specification.Operators.not;

import byteback.specification.Contract.Behavior;
import byteback.specification.Contract.Raise;
import byteback.specification.Contract.Return;

public class UnsupportedOperation {

	public static abstract class Map<K, V> {

		@Behavior
		public boolean is_immutable(final K key, final V value) {
			return this instanceof ImmutableMap;
		}

		@Behavior
		public boolean is_mutable(final K key, final V value) {
			return not(this instanceof ImmutableMap);
		}

		@Raise(exception = UnsupportedOperationException.class, when = "is_immutable")
		@Return(when = "is_mutable")
		public abstract void put(final K key, final V value);
		
	}

	public static class ImmutableMap<K, V> extends Map<K, V> {

		public void put(final K key, final V value) {
			throw new UnsupportedOperationException();
		}

	}

	public static class MutableMap<K, V> extends Map<K, V> {

		public void put(final K key, final V value) {
			// ...
		}

	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 6 verified, 0 errors
 */
