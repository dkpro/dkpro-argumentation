/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.tudarmstadt.ukp.math.Sparse3DObjectMatrix;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 30, 2016
 *
 */
@SuppressWarnings("deprecation")
public final class SpanAnnotationMatrices {

	private static final Log LOG = LogFactory.getLog(SpanAnnotationMatrices.class);

	public static <T extends SpanTextLabel> Sparse3DObjectMatrix<String, T> createMatrix(
			final Collection<T> spanAnnotationVector) {
		final Sparse3DObjectMatrix<String, T> result = new Sparse3DObjectMatrix<>(
				new Int2ObjectOpenHashMap<>(spanAnnotationVector.size() + 1));
		putAnnotations(result, spanAnnotationVector.stream());
		return result;

	}

	public static <T extends SpanTextLabel> Sparse3DObjectMatrix<String, T> createMatrix(
			final Collection<T> spanAnnotationVector, final int estimatedSpanBeginToEndMapMaxCapacity,
			final int estimatedAnnotationMapMaxCapacity) {
		final Sparse3DObjectMatrix<String, T> result = new Sparse3DObjectMatrix<>(
				new Int2ObjectOpenHashMap<>(spanAnnotationVector.size() + 1), estimatedSpanBeginToEndMapMaxCapacity,
				estimatedAnnotationMapMaxCapacity);
		putAnnotations(result, spanAnnotationVector.stream());
		return result;
	}

	public static <T extends SpanTextLabel> void putAnnotations(final Sparse3DObjectMatrix<? super String, T> result,
			final Stream<T> spanAnnotationVector) {
		spanAnnotationVector.forEach(spanAnnotation -> {
			final SpanText spanText = spanAnnotation.getTextSpan();
			final Span span = spanText.getSpan();
			final int begin = span.getBegin();
			final int end = span.getEnd();
			final Map<? super String, T> spanAnnotations = result.fetch3DMap(begin, end);
			final String label = spanAnnotation.getLabel();
			final T oldSpanAnnotation = spanAnnotations.put(label, spanAnnotation);
			if (oldSpanAnnotation != null) {
				LOG.warn(String.format("Annotation label \"%s\" already exists for span [%d, %d]; Overwriting.", label,
						begin, end));
			}
		});
	}

	private SpanAnnotationMatrices() {
	}

}
