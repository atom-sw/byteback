package soot.util;

import java.util.*;
import java.util.Map.Entry;

/**
 * A map with sets as values, HashMap implementation.
 *
 * @author Ondrej Lhotak
 */
public class HashMultiMap<K, V> extends AbstractMultiMap<K, V> {

    private static final long serialVersionUID = -1928446853508616896L;

    private static final float DEFAULT_LOAD_FACTOR = 0.7f;

    protected final Map<K, Set<V>> m;
    protected final float loadFactor;

    protected Map<K, Set<V>> createMap() {
        return createMap(0);
    }

    protected Map<K, Set<V>> createMap(int initialSize) {
        return new HashMap<K, Set<V>>(initialSize, loadFactor);
    }

    public HashMultiMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.m = createMap();
    }

    public HashMultiMap(int initialSize) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.m = createMap(initialSize);
    }

    public HashMultiMap(int initialSize, float loadFactor) {
        this.loadFactor = loadFactor;
        this.m = createMap(initialSize);
    }

    public HashMultiMap(MultiMap<K, V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.m = createMap();
        putAll(m);
    }

    public HashMultiMap(Map<K, Collection<V>> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        this.m = createMap();
        putAll(m);
    }

    @Override
    public int numKeys() {
        return m.size();
    }

    @Override
    public boolean containsKey(Object key) {
        return m.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        for (Set<V> s : m.values()) {
            if (s.contains(value)) {
                return true;
            }
        }
        return false;
    }

    protected Set<V> newSet() {
        return new HashSet<V>(4);
    }

    private Set<V> findSet(K key) {
        Set<V> s = m.get(key);
        if (s == null) {
            s = newSet();
            m.put(key, s);
        }
        return s;
    }

    @Override
    public boolean put(K key, V value) {
        return findSet(key).add(value);
    }

    @Override
    public boolean putAll(K key, Collection<V> values) {
        if (values.isEmpty()) {
            return false;
        }
        return findSet(key).addAll(values);
    }

    @Override
    public boolean remove(K key, V value) {
        Set<V> s = m.get(key);
        if (s == null) {
            return false;
        }
        boolean ret = s.remove(value);
        if (s.isEmpty()) {
            m.remove(key);
        }
        return ret;
    }

    @Override
    public boolean remove(K key) {
        return null != m.remove(key);
    }

    @Override
    public boolean removeAll(K key, Collection<V> values) {
        Set<V> s = m.get(key);
        if (s == null) {
            return false;
        }
        boolean ret = s.removeAll(values);
        if (s.isEmpty()) {
            m.remove(key);
        }
        return ret;
    }

    @Override
    public Set<V> get(K o) {
        Set<V> ret = m.get(o);
        return (ret == null) ? Collections.emptySet() : ret;
    }

    @Override
    public Set<K> keySet() {
        return m.keySet();
    }

    @Override
    public Set<V> values() {
        Set<V> ret = new HashSet<V>(m.size());
        for (Set<V> s : m.values()) {
            ret.addAll(s);
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MultiMap)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        MultiMap<K, V> mm = (MultiMap<K, V>) o;
        if (!keySet().equals(mm.keySet())) {
            return false;
        }
        Iterator<Map.Entry<K, Set<V>>> it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, Set<V>> e = it.next();
            Set<V> s = e.getValue();
            if (!s.equals(mm.get(e.getKey()))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return m.hashCode();
    }

    @Override
    public int size() {
        return m.size();
    }

    @Override
    public void clear() {
        m.clear();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<K, Set<V>> entry : m.entrySet()) {
            builder.append(entry.getKey()).append(":\n").append(entry.getValue().toString()).append("\n\n");
        }
        if (builder.length() > 2) {
            builder.delete(builder.length() - 2, builder.length());
        }
        return builder.toString();
    }
}
