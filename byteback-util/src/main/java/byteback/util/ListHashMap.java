package byteback.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListHashMap<K, V> extends HashMap<K, List<V>> {

	public void add(final K key, final V value) {
		super.computeIfAbsent(key, ($) -> new ArrayList<>()).add(0, value);
	}

}
