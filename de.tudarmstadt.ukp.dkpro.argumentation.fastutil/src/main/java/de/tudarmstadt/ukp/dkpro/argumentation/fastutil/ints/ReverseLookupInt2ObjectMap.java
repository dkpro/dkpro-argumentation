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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.tudarmstadt.ukp.dkpro.argumentation.collections.ListIndices;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * @author Todd Shore
 */
public final class ReverseLookupInt2ObjectMap<V>
    implements Int2ObjectMap<V>
{

    private final Int2ObjectMap<V> decorated;
    private final List<V> indexedValues;

    /**
     *
     */
    public ReverseLookupInt2ObjectMap(final Int2ObjectMap<V> decorated)
    {
        this.decorated = decorated;
        this.indexedValues = ListIntIndices
                .createListFromIndexMapping(decorated.int2ObjectEntrySet());
    }

    @Override
    public void clear()
    {
        decorated.clear();
        indexedValues.clear();
    }

    @Override
    public boolean containsKey(final int key)
    {
        return decorated.containsKey(key);
    }

    @Override
    public boolean containsKey(final Object key)
    {
        return decorated.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value)
    {
        return decorated.containsValue(value);
    }

    @Override
    public V defaultReturnValue()
    {
        return decorated.defaultReturnValue();
    }

    @Override
    public void defaultReturnValue(final V rv)
    {
        decorated.defaultReturnValue(rv);
    }

    @Override
    public ObjectSet<java.util.Map.Entry<Integer, V>> entrySet()
    {
        return decorated.entrySet();
    }

    @Override
    public V get(final int key)
    {
        return decorated.get(key);
    }

    @Override
    public V get(final Object key)
    {
        return decorated.get(key);
    }

    @Override
    public ObjectSet<it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<V>> int2ObjectEntrySet()
    {
        return decorated.int2ObjectEntrySet();
    }

    @Override
    public boolean isEmpty()
    {
        return decorated.isEmpty();
    }

    @Override
    public IntSet keySet()
    {
        return decorated.keySet();
    }

    @Override
    public V put(final int key, final V value)
    {
        final V putValue = decorated.put(key, value);

        ListIndices.ensureIndex(indexedValues, key);
        final V result = indexedValues.set(key, value);
        assert Objects.equals(putValue, result);
        return result;
    }

    @Override
    public V put(final Integer key, final V value)
    {
        assert key != null;
        return put(key.intValue(), value);
    }

    @Override
    public void putAll(final Map<? extends Integer, ? extends V> m)
    {
        assert m != null;

        decorated.putAll(m);

        // Find the maximum index in order to pre-set the list length
        final int maxIndex = Collections.max(m.keySet());
        ListIndices.ensureIndex(indexedValues, maxIndex);

        ListIndices.setIndexedElements(indexedValues, m);

        throw new RuntimeException(new UnsupportedOperationException("Not yet implemented"));
        // TODO Auto-generated method stub
    }

    @Override
    public V remove(final int key)
    {
        final V removedValue = decorated.remove(key);
        final V result = indexedValues.remove(key);
        assert Objects.equals(removedValue, result);
        return result;
    }

    @Override
    public V remove(final Object key)
    {
        final V result;

        if (key instanceof Integer) {
            result = remove(((Integer) key).intValue());
        }
        else {
            result = null;
        }

        return result;
    }

    @Override
    public int size()
    {
        return decorated.size();
    }

    @Override
    public ObjectCollection<V> values()
    {
        return decorated.values();
    }

}
