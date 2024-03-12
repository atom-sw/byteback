package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import heros.solver.Pair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A map with sets as values.
 *
 * @author Ondrej Lhotak
 */
public interface MultiMap<K, V> extends Iterable<Pair<K, V>> {
    boolean isEmpty();

    int numKeys();

    boolean contains(K key, V value);

    boolean containsKey(K key);

    boolean containsValue(V value);

    boolean put(K key, V value);

    boolean putAll(K key, Collection<V> values);

    boolean putAll(Map<K, Collection<V>> m);

    boolean putMap(Map<K, V> m);

    boolean putAll(MultiMap<K, V> m);

    boolean remove(K key, V value);

    boolean remove(K key);

    boolean removeAll(K key, Collection<V> values);

    Set<V> get(K o);

    Set<K> keySet();

    Set<V> values();

    /**
     * Gets the number of keys in this MultiMap
     *
     * @return The number of keys in this MultiMap
     */
    int size();

    void clear();
}
