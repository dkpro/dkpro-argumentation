/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.math.Sparse3DObjectMatrix;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 30, 2016
 *
 */
@SuppressWarnings("deprecation")
public final class SpanAnnotationMatrices {

	private static final Log LOG = LogFactory.getLog(SpanAnnotationMatrices.class);

	public static <T extends Span> List<T> createList(
			final ObjectSet<? extends Int2ObjectMap.Entry<? extends Int2ObjectMap<? extends Map<?, T>>>> objectSet,
			final int size) {
		// TODO: add an iterator method to Sparse3DObjectMatrix and use that
		// instead of doing external iteration here
		final List<T> result = new ArrayList<>(size);
		for (final Int2ObjectMap.Entry<? extends Int2ObjectMap<? extends Map<?, T>>> firstDimEntry : objectSet) {
			// final int begin = firstDimEntry.getIntKey();
			for (final Int2ObjectMap.Entry<? extends Map<?, T>> secondDimEntry : firstDimEntry.getValue()
					.int2ObjectEntrySet()) {
				// final int end = secondDimEntry.getIntKey();
				for (final Map.Entry<?, T> thirdDimEntry : secondDimEntry.getValue().entrySet()) {
					// final String annotationType = thirdDimEntry.getKey();
					final T annotation = thirdDimEntry.getValue();
					result.add(annotation);
				}
			}
		}
		return result;
	}

	public static <T extends AnnotatedSpan> Sparse3DObjectMatrix<String, T> createMatrix(
			final Collection<T> spanAnnotationVector) {
		final Sparse3DObjectMatrix<String, T> result = new Sparse3DObjectMatrix<>(
				new Int2ObjectOpenHashMap<>(spanAnnotationVector.size() + 1));
		putAnnotations(result, spanAnnotationVector);
		return result;

	}

	public static <T extends AnnotatedSpan> Sparse3DObjectMatrix<String, T> createMatrix(
			final Collection<T> spanAnnotationVector, final int estimatedSpanBeginToEndMapMaxCapacity,
			final int estimatedAnnotationMapMaxCapacity) {
		final Sparse3DObjectMatrix<String, T> result = new Sparse3DObjectMatrix<>(
				new Int2ObjectOpenHashMap<>(spanAnnotationVector.size() + 1), estimatedSpanBeginToEndMapMaxCapacity,
				estimatedAnnotationMapMaxCapacity);
		putAnnotations(result, spanAnnotationVector);
		return result;
	}

	public static <T extends AnnotatedSpan> void putAnnotations(final Sparse3DObjectMatrix<? super String, T> result,
			final Collection<T> spanAnnotationVector) {
		for (final T spanAnnotation : spanAnnotationVector) {
			final int begin = spanAnnotation.getBegin();
			final int end = spanAnnotation.getEnd();
			final Map<? super String, T> spanAnnotations = result.fetch3DMap(begin, end);
			final String annotationType = spanAnnotation.getAnnotationType();
			final T oldSpanAnnotation = spanAnnotations.put(annotationType, spanAnnotation);
			if (oldSpanAnnotation != null) {
				LOG.warn(String.format("Annotation type \"%s\" already exists for span [%d, %d]; Overwriting.",
						annotationType, begin, end));
			}
		}
	}

	private SpanAnnotationMatrices() {
	}

}
