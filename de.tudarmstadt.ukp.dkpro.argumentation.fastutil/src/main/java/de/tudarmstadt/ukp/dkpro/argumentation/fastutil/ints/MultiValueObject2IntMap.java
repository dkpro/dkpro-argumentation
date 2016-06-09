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
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.hamcrest.Factory;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntCollections;
import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;

/**
 * A {@link Map} which decorates another {@link Map}, which has
 * {@link IntCollection collections} of values for each key.
 *
 * @param <K>
 *            The key type.
 * @param <C>
 *            The type of {@code IntCollection} object used to contain the
 *            values for each key.
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 */
public final class MultiValueObject2IntMap<K, C extends IntCollection> implements Map<K, C>, Serializable {

	/**
	 * The generated serial version UID.
	 */
	private static final long serialVersionUID = 682678645646614813L;

	/**
	 * Increments the values for a given key which occur within a given range.
	 *
	 * @param multimap
	 *            The {@link MultiValueObject2IntMap} to add to.
	 * @param keysToIncrement
	 *            The keys to increment the values of.
	 * @param increment
	 *            The amount to increment the values by.
	 * @param fromValue
	 *            The inclusive minimum of the key values to increment.
	 * @param toValue
	 *            The exclusive maximum of the key values to increment.
	 */
	public static final <K, C extends IntSortedSet> void incrementValues(final MultiValueObject2IntMap<K, C> multimap,
			final Collection<? extends K> keysToIncrement, final int increment, final int fromValue,
			final int toValue) {
		assert keysToIncrement != null;
		final IntCollection incrementedValues = new IntOpenHashSet(keysToIncrement.size());
		for (final K keyToIncrement : keysToIncrement) {
			incrementValues(multimap, keyToIncrement, increment, fromValue, toValue, incrementedValues);
		}
	}

	/**
	 * Adds one value for each given key, incrementing the value added for the
	 * next key by one.
	 *
	 * @param multimap
	 *            The {@link MultiValueObject2IntMap} to add to.
	 * @param keysToAdd
	 *            The keys to add.
	 * @param startValue
	 *            The integer value to start at.
	 * @return If at least one value was added to the map.
	 */
	public static final <K, C extends IntCollection> boolean putIncrementingValues(
			final MultiValueObject2IntMap<K, C> multimap, final Iterable<? extends K> keysToAdd, int startValue) {
		assert keysToAdd != null;
		boolean result = false;

		for (final K keyToAdd : keysToAdd) {
			if (multimap.putValue(keyToAdd, startValue++)) {
				result = true;
			}
		}

		return result;
	}

	/**
	 * Increments the values for a given key which occur within a given range.
	 *
	 * @param multimap
	 *            The {@link MultiValueObject2IntMap} to add to.
	 * @param keysToIncrement
	 *            The keys to increment the values of.
	 * @param increment
	 *            The amount to increment the values by.
	 * @param fromValue
	 *            The inclusive minimum of the key values to increment.
	 * @param toValue
	 *            The exclusive maximum of the key values to increment.
	 * @param incrementedValues
	 *            A {@link IntCollection} of elements in the original mapping
	 *            which were incremented.
	 */
	private static final <K, C extends IntSortedSet> void incrementValues(final MultiValueObject2IntMap<K, C> multimap,
			final K keyToIncrement, final int increment, final int fromValue, final int toValue,
			final IntCollection incrementedValues) {
		// Filter out the values outside the specified range of values to update
		final IntIterable valuesToIncrement = multimap.get(keyToIncrement).subSet(fromValue, toValue);
		for (final int valueToIncrement : valuesToIncrement) {
			if (!incrementedValues.contains(valueToIncrement)) {
				// Remove the old value from the map
				final boolean wasOldValueRemoved = multimap.removeValue(keyToIncrement, valueToIncrement);
				assert wasOldValueRemoved;

				final int incrementedValue = valueToIncrement + increment;
				// Add the new value to the map
				final boolean wasNewValuePut = multimap.putValue(keyToIncrement, incrementedValue);
				assert wasNewValuePut;

				// Put the old value in the set of already-incremented
				// values
				final boolean wasAdded = incrementedValues.add(valueToIncrement);
				assert wasAdded;
			}
		}
	}

	/**
	 * A {@link IntCollection} of all elements for all keys in the
	 * {@link #getDecorated() decorated map}.
	 */
	private final transient IntCollection allValues;

	/**
	 * The decorated {@link Map} instance.
	 */
	private final Map<K, C> decorated;

	/**
	 * The {@link Factory} used for creating new value collections for the map
	 * keys.
	 */
	private final Supplier<? extends C> valueCollectionFactory;

	/**
	 * @param decorated
	 *            The {@link Map} to decorate.
	 * @param valueCollectionFactory
	 *            The {@link Factory} to use for creating new value collections
	 *            for the map keys.
	 */
	public MultiValueObject2IntMap(final Map<K, C> decorated, final Supplier<? extends C> valueCollectionFactory) {
		this.decorated = decorated;
		this.valueCollectionFactory = valueCollectionFactory;
		this.allValues = de.tudarmstadt.ukp.dkpro.argumentation.fastutil.ints.IntCollections
				.createAllElementSet(decorated.values());

	}

	@Override
	public void clear() {
		decorated.clear();
	}

	@Override
	public boolean containsKey(final Object key) {
		return decorated.containsKey(key);
	}

	/**
	 * Checks if a given value is mapped to a given key.
	 *
	 * @param key
	 *            The key to check the values of.
	 * @param value
	 *            The value to check.
	 * @return {@code true} iff the given key maps to the given value.
	 */
	public boolean containsValue(final K key, final int value) {
		final boolean result;

		final C keyValues = decorated.get(key);
		if (keyValues == null) {
			result = false;
		} else {
			result = keyValues.contains(value);
		}
		return result;
	}

	@Override
	public boolean containsValue(final Object value) {
		return allValues.contains(value);
	}

	@Override
	public Set<Entry<K, C>> entrySet() {
		return decorated.entrySet();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public final boolean equals(final Object obj) {
		final boolean result;

		if (this == obj) {
			result = true;
		} else if (obj == null) {
			result = false;
		} else if (obj instanceof MultiValueObject2IntMap<?, ?>) {
			final MultiValueObject2IntMap<?, ?> cast = (MultiValueObject2IntMap<?, ?>) obj;
			result = isEquivalentTo(cast);
		} else {
			result = false;
		}

		return result;
	}

	@Override
	public C get(final Object key) {
		return decorated.get(key);
	}

	/**
	 * @return An unmodifiable view of a {@link IntCollection} of all elements
	 *         for all keys in the {@link #getDecorated() decorated map}.
	 */
	public IntCollection getAllValues() {
		return IntCollections.unmodifiable(allValues);
	}

	/**
	 * @return An unmodifiable view of the decorated {@link Map} instance.
	 */
	public Map<K, C> getDecorated() {
		return Collections.unmodifiableMap(decorated);
	}

	/**
	 * Returns all value elements mapped to a key.
	 *
	 * @param key
	 *            The key to get all the elements for.
	 * @return A {@link IntCollection} of elements; if there is no mapping for
	 *         the given key, then an empty {@code IntCollection} is returned.
	 */
	public C getValues(final K key) {
		C result = decorated.get(key);
		if (result == null) {
			result = valueCollectionFactory.get();
			decorated.put(key, result);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (decorated == null ? 0 : decorated.hashCode());
		return result;
	}

	@Override
	public boolean isEmpty() {
		return decorated.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return decorated.keySet();
	}

	@Override
	public C put(final K key, final C value) {
		final C result = decorated.put(key, value);

		if (result != null) {
			allValues.removeAll(result);
		}
		allValues.addAll(value);

		return result;
	}

	@Override
	public void putAll(final Map<? extends K, ? extends C> m) {
		decorated.putAll(m);
		for (final C coll : m.values()) {
			allValues.addAll(coll);
		}
	}

	/**
	 * Adds a value for a given key.
	 *
	 * @param key
	 *            The key to add the new value to.
	 * @param value
	 *            The value to add.
	 * @return {@code true} iff the value was successfully added.
	 */
	public boolean putValue(final K key, final int value) {
		final C values = getValues(key);
		final boolean result = values.add(value);
		if (result) {
			allValues.add(value);
		}
		return result;
	}

	/**
	 * Adds values for a given key.
	 *
	 * @param key
	 *            The key to add the new values to.
	 * @param values
	 *            The values to add.
	 * @return {@code true} if at least one value was successfully added.
	 */
	public boolean putValues(final K key, final IntCollection values) {
		final C keyValues = getValues(key);
		final boolean result = keyValues.addAll(values);
		if (result) {
			allValues.addAll(values);
		}
		return result;
	}

	@Override
	public C remove(final Object key) {
		final C result = decorated.remove(key);

		if (result != null) {
			allValues.removeAll(result);
		}

		return result;
	}

	/**
	 * Removes a given value mapped to a given key.
	 *
	 * @param key
	 *            The key to remove the given mapped value for.
	 * @param value
	 *            The value to remove from the {@link IntCollection} of values
	 *            mapped to the given key.
	 * @return {@code true} iff the value was successfully removed.
	 */
	public boolean removeValue(final K key, final int value) {
		final boolean result;

		final C values = decorated.get(key);
		if (values == null) {
			result = false;
		} else {
			result = values.remove(value);
			if (result) {
				allValues.remove(value);
			}
		}

		return result;
	}

	/**
	 * Removes given values mapped to a given key.
	 *
	 * @param key
	 *            The key to remove the given mapped value for.
	 * @param values
	 *            The values to remove from the {@link IntCollection} of values
	 *            mapped to the given key.
	 * @return {@code true} if at least one value was successfully removed.
	 */
	public boolean removeValues(final K key, final IntCollection values) {
		final boolean result;
		final C keyValues = decorated.get(key);
		if (values == null) {
			result = false;
		} else {
			result = keyValues.remove(values);
			if (result) {
				allValues.removeAll(values);
			}
		}

		return result;
	}

	@Override
	public int size() {
		return decorated.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final String prefix = "MultiValueObject2IntMap [decorated=";
		final String decoratedRepr = decorated.toString();
		final StringBuilder builder = new StringBuilder(prefix.length() + decoratedRepr.length() + 1);
		builder.append(prefix);
		builder.append(decoratedRepr);
		builder.append(']');
		return builder.toString();
	}

	@Override
	public Collection<C> values() {
		return decorated.values();
	}

	private boolean isEquivalentTo(final MultiValueObject2IntMap<?, ?> other) {
		assert other != null;
		final Map<K, C> decorated = getDecorated();
		if (decorated == null) {
			if (other.decorated != null) {
				return false;
			}
		} else if (!decorated.equals(other.decorated)) {
			return false;
		}
		return true;
	}

}
