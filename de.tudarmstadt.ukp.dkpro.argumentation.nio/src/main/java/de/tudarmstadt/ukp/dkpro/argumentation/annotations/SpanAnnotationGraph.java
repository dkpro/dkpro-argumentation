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
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.tudarmstadt.ukp.dkpro.argumentation.fastutil.ints.ReverseLookupOrderedSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectSet;

/**
 * A class representing a
 * <a href="https://en.wikipedia.org/wiki/Directed_acyclic_graph">directed
 * acyclic graph</a> of {@link SpanTextLabel} objects.
 *
 *
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 2, 2016
 *
 * @param <T>
 *            The concrete type of {@code SpanTextLabel} used returned by
 *            methods in this class.
 *
 */
public final class SpanAnnotationGraph<T extends SpanTextLabel> {

	public static final String PROPERTY_RELATIONS = "relations";

	public static final String PROPERTY_SPAN_ANNOTATIONS = "spanAnnotations";

	private final int[] relationTransitionTable;

	private transient final Int2ObjectMap<? extends Int2ObjectMap<? extends Map<String, T>>> spanAnnotationMatrix;

	private final ReverseLookupOrderedSet<T> spanAnnotationVector;

	public SpanAnnotationGraph(
			final Int2ObjectMap<? extends Int2ObjectMap<? extends Map<String, T>>> spanAnnotationMatrix,
			final int spanAnnotationMatrixSize, final int[] relationTransitionTable) {
		this(new ReverseLookupOrderedSet<>(
				createList(spanAnnotationMatrix.int2ObjectEntrySet(), spanAnnotationMatrixSize)),
				spanAnnotationMatrix, relationTransitionTable);
	}
	
	private static <T> List<T> createList(
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

	@SuppressWarnings("deprecation")
	@JsonCreator
	public SpanAnnotationGraph(@JsonProperty(PROPERTY_SPAN_ANNOTATIONS) final List<T> spanAnnotationVector,
			@JsonProperty(PROPERTY_RELATIONS) final int[] relationTransitionTable) {
		this(spanAnnotationVector instanceof ReverseLookupOrderedSet ? (ReverseLookupOrderedSet<T>) spanAnnotationVector
				: new ReverseLookupOrderedSet<>(spanAnnotationVector),
				SpanAnnotationMatrices.createMatrix(spanAnnotationVector).getBackingMap(), relationTransitionTable);
	}

	public SpanAnnotationGraph(final List<T> spanAnnotationVector,
			final Int2ObjectMap<? extends Int2ObjectMap<? extends Map<String, T>>> spanAnnotationMatrix,
			final int[] relationTransitionTable) {
		this(spanAnnotationVector instanceof ReverseLookupOrderedSet ? (ReverseLookupOrderedSet<T>) spanAnnotationVector
				: new ReverseLookupOrderedSet<>(spanAnnotationVector), spanAnnotationMatrix, relationTransitionTable);
	}

	@SuppressWarnings("deprecation")
	public SpanAnnotationGraph(final ReverseLookupOrderedSet<T> spanAnnotationVector,
			final int[] relationTransitionTable) {
		this(spanAnnotationVector, SpanAnnotationMatrices.createMatrix(spanAnnotationVector).getBackingMap(),
				relationTransitionTable);
	}

	public SpanAnnotationGraph(final ReverseLookupOrderedSet<T> spanAnnotationVector,
			final Int2ObjectMap<? extends Int2ObjectMap<? extends Map<String, T>>> spanAnnotationMatrix,
			final int[] relationTransitionTable) {
		this.spanAnnotationVector = spanAnnotationVector;
		this.spanAnnotationMatrix = spanAnnotationMatrix;
		this.relationTransitionTable = relationTransitionTable;
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
		if (!(obj instanceof SpanAnnotationGraph)) {
			return false;
		}
		final SpanAnnotationGraph<?> other = (SpanAnnotationGraph<?>) obj;
		if (!Arrays.equals(relationTransitionTable, other.relationTransitionTable)) {
			return false;
		}
		if (spanAnnotationVector == null) {
			if (other.spanAnnotationVector != null) {
				return false;
			}
		} else if (!spanAnnotationVector.equals(other.spanAnnotationVector)) {
			return false;
		}
		return true;
	}

	public T get(final int id) {
		return spanAnnotationVector.get(id);
	}

	public int getId(final T spanAnnotation) {
		final Object2IntMap<T> spanAnnotationIds = spanAnnotationVector.getReverseLookupMap();
		return spanAnnotationIds.getInt(spanAnnotation);
	}

	public Map<String, T> getLabels(final Span span) {
		Map<String, T> result;
		final Int2ObjectMap<? extends Int2ObjectMap<? extends Map<String, T>>> annotMatrix = getSpanAnnotationMatrix();
		final int begin = span.getBegin();
		final Int2ObjectMap<? extends Map<String, T>> firstDim = annotMatrix.get(begin);
		if (firstDim == annotMatrix.defaultReturnValue()) {
			throw new NoSuchElementException(String.format("Span begin index %d not found in matrix.", begin));
		}
		final int end = span.getEnd();
		result = firstDim.get(end);
		if (result == firstDim.defaultReturnValue()) {
			throw new NoSuchElementException(String.format("Span end index %d not found in matrix.", end));
		}
		return result;

	}

	public T getRelationTarget(final T source) throws NoSuchElementException {
		final T result;

		final int sourceId = getId(source);
		if (sourceId < 0) {
			throw new NoSuchElementException("Not found in relation table: " + source);
		}
		final int targetId = relationTransitionTable[sourceId];
		result = targetId < 0 ? null : get(targetId);

		return result;
	}

	/**
	 * @return the relationTransitionTable
	 */
	@JsonProperty(PROPERTY_RELATIONS)
	public int[] getRelationTransitionTable() {
		return relationTransitionTable;
	}

	/**
	 * @return the spanAnnotationMatrix
	 */
	@JsonIgnore
	public Int2ObjectMap<? extends Int2ObjectMap<? extends Map<String, T>>> getSpanAnnotationMatrix() {
		return spanAnnotationMatrix;
	}

	/**
	 * @return the spanAnnotationVector
	 */
	@JsonProperty(PROPERTY_SPAN_ANNOTATIONS)
	public ReverseLookupOrderedSet<T> getSpanAnnotationVector() {
		return spanAnnotationVector;
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
		result = prime * result + Arrays.hashCode(relationTransitionTable);
		result = prime * result + (spanAnnotationVector == null ? 0 : spanAnnotationVector.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SpanAnnotationGraph [getSpanAnnotationVector()=");
		builder.append(getSpanAnnotationVector());
		builder.append(", getRelationTransitionTable()=");
		builder.append(Arrays.toString(getRelationTransitionTable()));
		builder.append("]");
		return builder.toString();
	}

}
