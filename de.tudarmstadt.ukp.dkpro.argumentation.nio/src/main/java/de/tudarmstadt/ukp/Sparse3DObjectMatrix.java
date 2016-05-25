/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Apr 29, 2016
 *
 */
public final class Sparse3DObjectMatrix<K, V> {

	private final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap;

	private final Supplier<V> defaultValueSupplier;

	private final int initialCapacity2D;

	private final int initialCapacity3D;

	public Sparse3DObjectMatrix(final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap,
			final int estimatedMaxCapacity2D, final int estimatedMaxCapacity3D) {
		this(backingMap, estimatedMaxCapacity2D, estimatedMaxCapacity3D, new Supplier<V>() {
			@Override
			public V get() {
				return null;
			}
		});
	}

	/**
	 *
	 */
	public Sparse3DObjectMatrix(final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap,
			final int estimatedMaxCapacity2D, final int estimatedMaxCapacity3D,
			final Supplier<V> defaultValueSupplier) {
		this.backingMap = backingMap;
		this.initialCapacity2D = estimatedMaxCapacity2D + 1;
		this.initialCapacity3D = estimatedMaxCapacity3D + 1;
		this.defaultValueSupplier = defaultValueSupplier;
	}

	public Int2ObjectMap<Map<K, V>> fetch2D(final int firstDimensionIdx) {
		Int2ObjectMap<Map<K, V>> result = get2D(firstDimensionIdx);
		if (result == null) {
			result = new Int2ObjectOpenHashMap<>(initialCapacity2D);
			backingMap.put(firstDimensionIdx, result);
		}
		return result;
	}

	public Map<K, V> fetch3DMap(final int firstDimensionIdx, final int secondDimensionIdx) {
		final Int2ObjectMap<Map<K, V>> secondDimensionMap = fetch2D(firstDimensionIdx);
		final Map<K, V> result = fetch3DMap(secondDimensionMap, secondDimensionIdx);
		return result;
	}

	public Map<K, V> fetch3DMap(final Int2ObjectMap<Map<K, V>> secondDimensionMap, final int secondDimensionIdx) {
		Map<K, V> result = secondDimensionMap.get(secondDimensionIdx);
		if (result == null) {
			result = new HashMap<>(initialCapacity3D);
			secondDimensionMap.put(secondDimensionIdx, result);
		}
		return result;
	}

	public V fetch3DValue(final int firstDimensionIdx, final int secondDimensionIdx, final K thirdDimensionKey) {
		final Map<K, V> thirdDimensionMap = fetch3DMap(firstDimensionIdx, secondDimensionIdx);
		return thirdDimensionMap.computeIfAbsent(thirdDimensionKey, key -> defaultValueSupplier.get());
	}

	public Int2ObjectMap<Map<K, V>> get2D(final int firstDimensionIdx) {
		return backingMap.get(firstDimensionIdx);
	}

	public Map<K, V> get3DMap(final int firstDimensionIdx, final int secondDimensionIdx) {
		final Int2ObjectMap<Map<K, V>> secondDimensionMap = get2D(firstDimensionIdx);
		return secondDimensionMap == null ? null : secondDimensionMap.get(secondDimensionIdx);
	}

	public V get3DValue(final int firstDimensionIdx, final int secondDimensionIdx, final K thirdDimensionKey) {
		final Map<K, V> thirdDimensionMap = get3DMap(firstDimensionIdx, secondDimensionIdx);
		return thirdDimensionMap == null ? null : thirdDimensionMap.get(thirdDimensionKey);
	}

}
