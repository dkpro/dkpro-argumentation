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

import java.util.Collection;

import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 */
public final class Object2IntMapValueArithmetic {

	/**
	 * Increments the values for a given key which occur within a given range.
	 *
	 * @param map
	 *            The {@link Object2IntMap} to add to.
	 * @param keysToIncrement
	 *            The keys to increment the values of.
	 * @param increment
	 *            The amount to increment the values by.
	 * @param fromValue
	 *            The inclusive minimum of the key values to increment.
	 * @param toValue
	 *            The exclusive maximum of the key values to increment.
	 */
	public static final <K> void incrementValues(final Object2IntMap<K> map,
			final Collection<? extends K> keysToIncrement, final int increment, final int fromValue,
			final int toValue) {
		assert keysToIncrement != null;
		final IntCollection incrementedValues = new IntOpenHashSet(keysToIncrement.size());
		for (final K keyToIncrement : keysToIncrement) {
			incrementValues(map, keyToIncrement, increment, fromValue, toValue, incrementedValues);
		}
	}

	/**
	 * Adds one value for each given key, incrementing the value added for the
	 * next key by one.
	 *
	 * @param map
	 *            The {@link Object2IntMap} to add to.
	 * @param keysToAdd
	 *            The keys to add.
	 * @param startValue
	 *            The integer value to start at.
	 */
	public static final <K> void putIncrementingValues(final Object2IntMap<K> map,
			final Iterable<? extends K> keysToAdd, final int startValue) {
		assert keysToAdd != null;

		{
			int currentValue = startValue;
			for (final K keyToAdd : keysToAdd) {
				map.put(keyToAdd, currentValue++);
			}
		}
	}

	/**
	 * Increments the values for a given key which occur within a given range.
	 *
	 * @param map
	 *            The {@link Object2IntMap} to add to.
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
	private static final <K> void incrementValues(final Object2IntMap<K> map, final K keyToIncrement,
			final int increment, final int fromValue, final int toValue, final IntCollection incrementedValues) {
		final int valueToIncrement = map.getInt(keyToIncrement);
		// Filter out the values outside the specified range of values to update
		if (fromValue <= valueToIncrement && valueToIncrement <= toValue
				&& !incrementedValues.contains(valueToIncrement)) {
			final int incrementedValue = valueToIncrement + increment;
			// Put the new value into the map
			final int oldValue = map.put(keyToIncrement, incrementedValue);
			assert oldValue == valueToIncrement;

			// Put the old value in the set of already-incremented
			// values
			final boolean wasAdded = incrementedValues.add(valueToIncrement);
			assert wasAdded;
		}
	}

	private Object2IntMapValueArithmetic() {
	}

}
