/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * TODO: Replace this with an object matrix implementation from the library
 * ultimately used for clustering in order to reduce the amount of code which
 * needs to be written.
 * 
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Apr 29, 2016
 *
 */
public final class Sparse3DObjectMatrix<K, V> implements Iterable<ThreeDMatrixEntry<K, V>> {

	private static <K, V> Supplier<Int2ObjectMap<Map<K, V>>> createDefault2DMapFactory() {
		return Int2ObjectOpenHashMap::new;
	}

	private static <K, V> Supplier<Map<K, V>> createDefault3DMapFactory() {
		return HashMap::new;
	}

	private static <V> Supplier<V> createNullValueSupplier() {
		return () -> null;
	}

	private final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap;

	private final Supplier<V> defaultValueSupplier;

	private final Supplier<Int2ObjectMap<Map<K, V>>> secondDimensionMapFactory;

	private long size = 0;

	private Supplier<Map<K, V>> thirdDimensionMapFactory;

	public Sparse3DObjectMatrix(final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap) {
		this(backingMap, createDefault2DMapFactory(), createDefault3DMapFactory(), createNullValueSupplier());
	}

	public Sparse3DObjectMatrix(final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap,
			final int estimatedMaxCapacity2D, final int estimatedMaxCapacity3D) {
		this(backingMap, estimatedMaxCapacity2D, estimatedMaxCapacity3D, createNullValueSupplier());
	}

	public Sparse3DObjectMatrix(final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap,
			final int estimatedMaxCapacity2D, final int estimatedMaxCapacity3D,
			final Supplier<V> defaultValueSupplier) {
		this(backingMap, () -> new Int2ObjectOpenHashMap<>(estimatedMaxCapacity2D + 1),
				() -> new HashMap<>(estimatedMaxCapacity3D + 1), defaultValueSupplier);
	}

	public Sparse3DObjectMatrix(final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap,
			final Supplier<Int2ObjectMap<Map<K, V>>> secondDimensionMapFactory,
			final Supplier<Map<K, V>> thirdDimensionMapFactory) {
		this(backingMap, secondDimensionMapFactory, thirdDimensionMapFactory, createNullValueSupplier());
	}

	public Sparse3DObjectMatrix(final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap,
			final Supplier<Int2ObjectMap<Map<K, V>>> secondDimensionMapFactory,
			final Supplier<Map<K, V>> thirdDimensionMapFactory, final Supplier<V> defaultValueSupplier) {
		this.backingMap = backingMap;
		this.secondDimensionMapFactory = secondDimensionMapFactory;
		this.thirdDimensionMapFactory = thirdDimensionMapFactory;
		this.defaultValueSupplier = defaultValueSupplier;
	}

	public Sparse3DObjectMatrix(final Int2ObjectMap<Int2ObjectMap<Map<K, V>>> backingMap,
			final Supplier<V> defaultValueSupplier) {
		this(backingMap, createDefault2DMapFactory(), createDefault3DMapFactory(), defaultValueSupplier);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Sparse3DObjectMatrix)) {
			return false;
		}
		final Sparse3DObjectMatrix<?, ?> other = (Sparse3DObjectMatrix<?, ?>) obj;
		if (backingMap == null) {
			if (other.backingMap != null) {
				return false;
			}
		} else if (!backingMap.equals(other.backingMap)) {
			return false;
		}
		if (defaultValueSupplier == null) {
			if (other.defaultValueSupplier != null) {
				return false;
			}
		} else if (!defaultValueSupplier.equals(other.defaultValueSupplier)) {
			return false;
		}
		if (secondDimensionMapFactory == null) {
			if (other.secondDimensionMapFactory != null) {
				return false;
			}
		} else if (!secondDimensionMapFactory.equals(other.secondDimensionMapFactory)) {
			return false;
		}
		if (thirdDimensionMapFactory == null) {
			if (other.thirdDimensionMapFactory != null) {
				return false;
			}
		} else if (!thirdDimensionMapFactory.equals(other.thirdDimensionMapFactory)) {
			return false;
		}
		return true;
	}

	public Int2ObjectMap<Map<K, V>> fetch2D(final int firstDimensionIdx) {
		Int2ObjectMap<Map<K, V>> result = get2D(firstDimensionIdx);
		if (result == null) {
			result = secondDimensionMapFactory.get();
			backingMap.put(firstDimensionIdx, result);
		}
		return result;
	}

	public Map<K, V> fetch3DMap(final int firstDimensionIdx, final int secondDimensionIdx) {
		final Int2ObjectMap<Map<K, V>> secondDimensionMap = fetch2D(firstDimensionIdx);
		final Map<K, V> result = fetch3DMap(secondDimensionMap, secondDimensionIdx);
		return result;
	}

	public V fetch3DValue(final int firstDimensionIdx, final int secondDimensionIdx, final K thirdDimensionKey) {
		final Map<K, V> thirdDimensionMap = fetch3DMap(firstDimensionIdx, secondDimensionIdx);
		return thirdDimensionMap.computeIfAbsent(thirdDimensionKey, key -> {
			final V result = defaultValueSupplier.get();
			size++;
			return result;
		});
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

	/**
	 * @return the backingMap
	 */
	public Int2ObjectMap<Int2ObjectMap<Map<K, V>>> getBackingMap() {
		return backingMap;
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
		result = prime * result + (backingMap == null ? 0 : backingMap.hashCode());
		result = prime * result + (defaultValueSupplier == null ? 0 : defaultValueSupplier.hashCode());
		result = prime * result + (secondDimensionMapFactory == null ? 0 : secondDimensionMapFactory.hashCode());
		result = prime * result + (thirdDimensionMapFactory == null ? 0 : thirdDimensionMapFactory.hashCode());
		return result;
	}

	@Override
	public Iterator<ThreeDMatrixEntry<K, V>> iterator() {
		throw new UnsupportedOperationException("Not yet implemented.");
		// return new Iterator<ThreeDMatrixEntry<K, V>>() {
		//
		// private Iterator<Entry<Int2ObjectMap<Map<K, V>>>> firstDimIter =
		// backingMap.int2ObjectEntrySet().iterator();
		//
		// private Iterator<Entry<Map<K, V>>> secondDimIter;
		//
		// private Entry<Map<K, V>> currentSecondDimEntry;
		//
		// private Iterator<Map.Entry<K, V>> thirdDimIter;
		//
		// @Override
		// public boolean hasNext() {
		// // TODO Auto-generated method stub
		// return false;
		// }
		//
		// @Override
		// public ThreeDMatrixEntry<K, V> next() {
		// Map.Entry<K, V> result;
		// if (thirdDimIter.hasNext()) {
		// result = thirdDimIter.next();
		// } else if (secondDimIter.hasNext()) {
		// currentSecondDimEntry = secondDimIter.next();
		// }
		//
		// for (Entry<Int2ObjectMap<Map<K, V>>> firstDim)
		// // TODO Auto-generated method stub
		// return null;
		// }
		//
		// };
	}

	public long size() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Sparse3DObjectMatrix [backingMap=");
		builder.append(backingMap);
		builder.append("]");
		return builder.toString();
	}

	private Map<K, V> fetch3DMap(final Int2ObjectMap<Map<K, V>> secondDimensionMap, final int secondDimensionIdx) {
		Map<K, V> result = secondDimensionMap.get(secondDimensionIdx);
		if (result == null) {
			result = thirdDimensionMapFactory.get();
			secondDimensionMap.put(secondDimensionIdx, result);
		}
		return result;
	}

}
