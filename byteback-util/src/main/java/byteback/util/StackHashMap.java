package byteback.util;

import java.util.HashMap;
import java.util.Stack;

public class StackHashMap<K, V> extends HashMap<K, Stack<V>> {

	public void add(final K key, final V value) {
		super.computeIfAbsent(key, ($) -> new Stack<>()).add(value);
	}

}
