package byteback.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CountingMapTest {

	@Test
	public void GetAccessCount_GivenNewEntry_YieldsSingleAccessCountOf1() {
		final CountingHashMap<Object, Object> map = new CountingHashMap<>();
		final Object key = new Object();
		final Object value = new Object();
		map.put(key, value);
		assertEquals((int) map.getAccessCount().get(key), 1);
	}

	@Test
	public void GetAccessCount_GivenDoublyAccessedEntry_YieldsSingleAccessCountOf2() {
		final CountingHashMap<Object, Object> map = new CountingHashMap<>();
		final Object key = new Object();
		final Object value1 = new Object();
		final Object value2 = new Object();
		map.put(key, value1);
		map.put(key, value2);
		assertEquals((int) map.getAccessCount().get(key), 2);
	}

	@Test
	public void GetAccessCount_GivenDoublyAccessedEntry_YieldsDoubleAccessCountOf1() {
		final CountingHashMap<Object, Object> map = new CountingHashMap<>();
		final Object key1 = new Object();
		final Object key2 = new Object();
		final Object value = new Object();
		map.put(key1, value);
		map.put(key2, value);
		assertEquals((int) map.getAccessCount().get(key1), 1);
		assertEquals((int) map.getAccessCount().get(key2), 1);
	}

}
