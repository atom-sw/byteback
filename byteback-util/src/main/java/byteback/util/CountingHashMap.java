package byteback.util;

import java.util.HashMap;
import java.util.Map;

public class CountingHashMap<K, V> extends HashMap<K, V> {

	private final Map<K, Integer> accessCount;

	public CountingHashMap() {
		this.accessCount = new HashMap<>();
	}

	@Override
	public V put(final K key, final V value) {
		accessCount.put(key, accessCount.getOrDefault(key, 0) + 1);

		return super.put(key, value);
	}

	public Map<K, Integer> getAccessCount() {
		return accessCount;
	}

}
