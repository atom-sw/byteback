package byteback.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SetHashMap<K, V> extends HashMap<K, Set<V>> {

	public void add(final K key, final V value) {
		super.computeIfAbsent(key, ($) -> new HashSet<>()).add(value);
	}

}
