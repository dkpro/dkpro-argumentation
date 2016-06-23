/*
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.fastutil.ints;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;

import it.unimi.dsi.fastutil.ints.IntSortedSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;

/**
 * A combined {@link List} and {@link Set} implementation which decorates a {@link List} instance,
 * maintaining a {@link Object2IntMap} which has the list elements {@code E} as keys which are
 * mapped to {@link IntSortedSet} objects containing the indices at which each element occurs in the
 * decorated {@code List}.
 *
 * @param <E>
 *            The type of the elements of the decorated {@code List}.
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 */
public final class ReverseLookupOrderedSet<E>
    implements List<E>, Serializable, Set<E>
{

    /**
     * The generated serial version UID.
     */
    private static final long serialVersionUID = -183547332772018900L;

    /**
     * The decorated {@link List} instance.
     */
    private final List<E> decorated;

    /**
     * The reverse-lookup index map for the elements of {@link #decorated the decorated
     * <code>List</code>}.
     */
    private transient Object2IntMap<E> reverseLookupMap;

    /**
     * @param decorated
     *            The {@link List} to decorate.
     */
    public ReverseLookupOrderedSet(final List<E> decorated)
    {
        this.decorated = decorated;

        this.reverseLookupMap = createNewReverseLookupMap();
    }

    @Override
    public boolean add(final E element)
    {
        final boolean result;

        if (contains(element)) {
            result = false;
        }
        else {
            final int index = decorated.size();
            result = decorated.add(element);
            if (result) {
                final int oldValue = reverseLookupMap.put(element, index);
                assert oldValue == reverseLookupMap.defaultReturnValue();
            }
        }

        return result;
    }

    @Override
    public void add(final int index, final E element)
    {
        decorated.add(index, element);
        // shift all indices in map after "index"
        final int lastIndex = decorated.size();
        final List<E> shiftedElements = decorated.subList(index + 1, lastIndex);
        Object2IntMapValueArithmetic.incrementValues(reverseLookupMap, shiftedElements, 1, index,
                lastIndex);
        // Put the new element into the reverse-lookup map after shifting the
        // existing elements because it is possible that the element is present
        // elsewhere in the list and so already has entries in the map
        final int oldValue = reverseLookupMap.put(element, index);
        assert oldValue == reverseLookupMap.defaultReturnValue();
    }

    @Override
    public boolean addAll(final Collection<? extends E> c)
    {
        final int lowestNewIndex = decorated.size();
        final boolean result = decorated.addAll(c);
        if (result) {
            // Use a sublist of the decorated list for updating the
            // reverse-lookup map instead of using the argument collection
            // directly because it is possible that not all elements from the
            // argument collection were successfully added
            final int highestNewIndex = decorated.size();
            final List<E> addedElements = decorated.subList(lowestNewIndex, highestNewIndex);
            Object2IntMapValueArithmetic.putIncrementingValues(reverseLookupMap, addedElements,
                    lowestNewIndex);
        }

        return result;
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c)
    {
        final int oldSize = decorated.size();
        final boolean result = decorated.addAll(index, c);
        if (result) {
            // Use the difference of the new size from the old size because it
            // is possible that not every single element from "c" was
            // successfully added
            final int addedElementCount = decorated.size() - oldSize;
            // Shift indices of existing elements in the reverse-lookup map
            // first to avoid possible key-value pair clashes
            final int lastShiftedElementOldIndex = index + addedElementCount;
            final List<E> shiftedElements = decorated.subList(index, lastShiftedElementOldIndex);
            Object2IntMapValueArithmetic.incrementValues(reverseLookupMap, shiftedElements,
                    addedElementCount, index, lastShiftedElementOldIndex);
            // Put the new elements into the reverse-lookup map after shifting
            // the
            // existing elements because it is possible that the elements are
            // present
            // elsewhere in the list and so already have entries in the map
            Object2IntMapValueArithmetic.putIncrementingValues(reverseLookupMap, c, index);
        }

        return result;
    }

    @Override
    public void clear()
    {
        decorated.clear();
        reverseLookupMap.clear();
    }

    @Override
    public boolean contains(final Object o)
    {
        return reverseLookupMap.containsKey(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c)
    {
        return reverseLookupMap.keySet().containsAll(c);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public final boolean equals(final Object obj)
    {
        final boolean result;

        if (this == obj) {
            result = true;
        }
        else if (obj == null) {
            result = false;
        }
        else if (obj instanceof ReverseLookupOrderedSet<?>) {
            final ReverseLookupOrderedSet<?> cast = (ReverseLookupOrderedSet<?>) obj;
            result = isEquivalentTo(cast);
        }
        else {
            result = false;
        }

        return result;
    }

    @Override
    public E get(final int index)
    {
        return decorated.get(index);
    }

    /**
     * @return An unmodifiable view of the decorated {@link List}.
     */
    public List<E> getDecorated()
    {
        return Collections.unmodifiableList(decorated);
    }

    /**
     * @return An unmodifiable view of the reverse-lookup index map for the elements of
     *         {@link #getDecorated() the decorated <code>List</code>}.
     */
    public Object2IntMap<E> getReverseLookupMap()
    {
        return Object2IntMaps.unmodifiable(reverseLookupMap);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (decorated == null ? 0 : decorated.hashCode());
        return result;
    }

    @Override
    public int indexOf(final Object o)
    {
        return reverseLookupMap.getInt(o);
    }

    @Override
    public boolean isEmpty()
    {
        return decorated.isEmpty();
    }

    @Override
    public Iterator<E> iterator()
    {
        return decorated.iterator();
    }

    @Override
    public int lastIndexOf(final Object o)
    {
        return reverseLookupMap.getInt(o);
    }

    @Override
    public ListIterator<E> listIterator()
    {
        return decorated.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index)
    {
        return decorated.listIterator(index);
    }

    @Override
    public E remove(final int index)
    {
        final E result = decorated.remove(index);
        // Remove the element from the reverse-lookup map before shifting the
        // remaining elements because it is possible that the element is present
        // elsewhere in the list and so still has entries in the map
        final Integer oldVal = reverseLookupMap.remove(result);
        assert oldVal != null && oldVal.equals(index);
        // shift all indices in map after "index"
        final int lastIndex = decorated.size();
        final List<E> shiftedElements = decorated.subList(index + 1, lastIndex);
        Object2IntMapValueArithmetic.incrementValues(reverseLookupMap, shiftedElements, -1, index,
                lastIndex);

        return result;
    }

    @Override
    public boolean remove(final Object o)
    {
        final int indexToRemove = indexOf(o);
        final boolean result;
        if (indexToRemove < 0) {
            result = false;
        }
        else {
            final E removedElement = remove(indexToRemove);
            assert Objects.equals(o, removedElement);
            result = true;
        }

        return result;

    }

    @Override
    public boolean removeAll(final Collection<?> c)
    {
        final boolean result = decorated.removeAll(c);
        if (result) {
            // Update the entire reverse-lookup map because it's simpler than
            // trying to deduce which elements were successfully removed
            reverseLookupMap = createNewReverseLookupMap();
        }

        return result;
    }

    @Override
    public boolean retainAll(final Collection<?> c)
    {
        final boolean result = decorated.retainAll(c);
        if (result) {
            // Update the entire reverse-lookup map because it's simpler than
            // trying to deduce which elements were successfully removed
            reverseLookupMap = createNewReverseLookupMap();
        }

        return result;
    }

    @Override
    public E set(final int index, final E element)
    {
        final E result = decorated.set(index, element);

        // Remove the old key-index pair from the reverse-lookup map
        final Integer oldVal = reverseLookupMap.remove(result);
        assert oldVal != null && oldVal.equals(index);
        // Put the new key-index pair into the reverse-lookup map
        final int oldValue = reverseLookupMap.put(element, index);
        assert oldValue == reverseLookupMap.defaultReturnValue();

        return result;
    }

    @Override
    public int size()
    {
        return decorated.size();
    }

    @Override
    public Spliterator<E> spliterator()
    {
        return List.super.spliterator();
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex)
    {
        return decorated.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray()
    {
        return decorated.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a)
    {
        return decorated.toArray(a);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final String decoratedFieldPrefix = "ReverseLookupOrderedSet [decorated=";
        final String decoratedStr = Objects.toString(decorated);
        final String suffix = "]";
        final StringBuilder builder = new StringBuilder(
                decoratedFieldPrefix.length() + decoratedStr.length() + suffix.length());
        builder.append(decoratedFieldPrefix);
        builder.append(decoratedStr);
        builder.append(suffix);
        return builder.toString();
    }

    private Object2IntMap<E> createNewReverseLookupMap()
    {
        return ListIntIndices.createOrderedSetIndexMap(decorated);
    }

    private boolean isEquivalentTo(final ReverseLookupOrderedSet<?> other)
    {
        assert other != null;

        return Objects.equals(decorated, other.decorated);
    }

}
