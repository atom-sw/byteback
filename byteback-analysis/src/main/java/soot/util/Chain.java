package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville
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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Augmented data type guaranteeing O(1) insertion and removal from a set of ordered, unique elements.
 *
 * @param <E> element type
 */
public interface Chain<E> extends Collection<E>, Serializable {

    /**
     * Inserts <code>toInsert</code> in the Chain before <code>point</code>.
     */
    void insertBefore(E toInsert, E point);

    /**
     * Inserts <code>toInsert</code> in the Chain after <code>point</code>.
     */
    void insertAfter(E toInsert, E point);

    /**
     * Inserts <code>toInsert</code> in the Chain before <code>point</code>.
     */
    void insertBefore(Chain<E> toInsert, E point);

    /**
     * Inserts <code>toInsert</code> in the Chain after <code>point</code>.
     */
    void insertAfter(Chain<E> toInsert, E point);

    /**
     * Inserts <code>toInsert</code> in the Chain before <code>point</code>.
     */
    void insertBefore(List<E> toInsert, E point);

    /**
     * Inserts <code>toInsert</code> in the Chain after <code>point</code>.
     */
    void insertAfter(List<E> toInsert, E point);

    /**
     * Inserts <code>toInsert</code> in the Chain before <code>point</code>.
     */
    void insertBefore(Collection<? extends E> toInsert, E point);

    /**
     * Inserts <code>toInsert</code> in the Chain after <code>point</code>.
     */
    void insertAfter(Collection<? extends E> toInsert, E point);

    /**
     * Replaces <code>out</code> in the Chain by <code>in</code>.
     */
    void swapWith(E out, E in);

    /**
     * Removes the given object from this Chain. Parameter has to be of type {@link Object} to be compatible with the
     * {@link Collection} interface.
     */
    @Override
    boolean remove(Object u);

    /**
     * Adds the given object at the beginning of the Chain.
     */
    void addFirst(E u);

    /**
     * Adds the given object at the end of the Chain.
     */
    void addLast(E u);

    /**
     * Removes the first object contained in this Chain.
     */
    void removeFirst();

    /**
     * Removes the last object contained in this Chain.
     */
    void removeLast();

    /**
     * Returns true if object <code>someObject</code> follows object <code>someReferenceObject</code> in the Chain, i.e.
     * someReferenceObject comes first and then someObject.
     */
    boolean follows(E someObject, E someReferenceObject);

    /**
     * Returns the first object in this Chain.
     */
    E getFirst();

    /**
     * Returns the last object in this Chain.
     */
    E getLast();

    /**
     * Returns the object immediately following <code>point</code>.
     */
    E getSuccOf(E point);

    /**
     * Returns the object immediately preceding <code>point</code>.
     */
    E getPredOf(E point);

    /**
     * Returns an iterator over a copy of this chain. This avoids ConcurrentModificationExceptions from being thrown if the
     * underlying Chain is modified during iteration. Do not use this to remove elements which have not yet been iterated over!
     */
    Iterator<E> snapshotIterator();

    /**
     * Returns an iterator over this Chain.
     */
    @Override
    Iterator<E> iterator();

    /**
     * Returns an iterator over this Chain, starting at the given object.
     */
    Iterator<E> iterator(E u);

    /**
     * Returns an iterator over this Chain, starting at head and reaching tail (inclusive).
     */
    Iterator<E> iterator(E head, E tail);

    /**
     * Returns the size of this Chain.
     */
    @Override
    int size();

    /**
     * Returns the number of times this chain has been modified.
     */
    long getModificationCount();

    /**
     * Gets all elements in the chain. There is no guarantee on sorting. On the other hand, the collection returned by this
     * method is thread-safe. You can iterate over it even in the case of concurrent modifications to the underlying chain.
     *
     * @return All elements in the chain in an unsorted collection
     */
    Collection<E> getElementsUnsorted();

}
