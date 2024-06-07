package byteback.common.collection;

import java.util.HashMap;
import java.util.HashSet;

public class SetHashMap<K, V> extends HashMap<K, HashSet<V>> {

	public void add(final K key, final V value) {
		super.computeIfAbsent(key, ($) -> new HashSet<>()).add(value);
	}

}
