/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 30, 2016
 *
 */
public final class ObjectMatrices {

	public static <T> List<T> createList(
			final ObjectSet<? extends Int2ObjectMap.Entry<? extends Int2ObjectMap<? extends Map<?, T>>>> matrixRows,
			final int size) {
		final List<T> result = new ArrayList<>(size);
		for (final Int2ObjectMap.Entry<? extends Int2ObjectMap<? extends Map<?, T>>> firstDimEntry : matrixRows) {
			for (final Int2ObjectMap.Entry<? extends Map<?, T>> secondDimEntry : firstDimEntry.getValue()
					.int2ObjectEntrySet()) {
				for (final Map.Entry<?, T> thirdDimEntry : secondDimEntry.getValue().entrySet()) {
					final T thirdDimValue = thirdDimEntry.getValue();
					result.add(thirdDimValue);
				}
			}
		}
		return result;
	}

	private ObjectMatrices() {
	}

}
