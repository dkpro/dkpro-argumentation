/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import java.util.Map;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.tudarmstadt.ukp.dkpro.argumentation.fastutil.ints.ReverseLookupOrderedSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

/**
 * This is essentially a labelled
 * <a href="https://en.wikipedia.org/wiki/Directed_acyclic_graph">directed
 * acyclic graph</a>.
 *
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 2, 2016
 *
 */
public final class SpanAnnotationGraph<T extends SpanAnnotation> {

	private final int[] relationTransitionTable;

	private transient final Int2ObjectMap<Int2ObjectMap<Map<String, T>>> spanAnnotationMatrix;

	private final ReverseLookupOrderedSet<T> spanAnnotationVector;

	/**
	 * TODO: Make a constructor
	 * {@code SpanAnnotationGraph(ReverseLookupObject2IntList<T>,
	 * int[])} which generates a matrix based on a {@link Int2ObjectMap}
	 * instance for passing to this constructor
	 *
	 * @param spanAnnotationVector
	 *
	 */
	public SpanAnnotationGraph(final ReverseLookupOrderedSet<T> spanAnnotationVector,
			final Int2ObjectMap<Int2ObjectMap<Map<String, T>>> spanAnnotationMatrix,
			final int[] relationTransitionTable) {
		this.spanAnnotationVector = spanAnnotationVector;
		this.spanAnnotationMatrix = spanAnnotationMatrix;
		this.relationTransitionTable = relationTransitionTable;
	}

	public T get(final int id) {
		return spanAnnotationVector.get(id);
	}

	public int getId(final T spanAnnotation) {
		final Object2IntMap<T> spanAnnotationIds = spanAnnotationVector.getReverseLookupMap();
		return spanAnnotationIds.getInt(spanAnnotation);
	}

	public T getRelationTarget(final T source) {
		final T result;

		final int sourceId = getId(source);
		if (sourceId < 0) {
			throw new NoSuchElementException("Not found in relation table: " + source);
		} else {
			final int targetId = relationTransitionTable[sourceId];
			result = get(targetId);
		}

		return result;
	}

	/**
	 * @return the relationTransitionTable
	 */
	@JsonProperty("relations")
	public int[] getRelationTransitionTable() {
		return relationTransitionTable;
	}

	/**
	 * @return the spanAnnotationMatrix
	 */
	@JsonIgnore
	public Int2ObjectMap<Int2ObjectMap<Map<String, T>>> getSpanAnnotationMatrix() {
		return spanAnnotationMatrix;
	}

	/**
	 * @return the spanAnnotationVector
	 */
	@JsonProperty("spanAnnotations")
	public ReverseLookupOrderedSet<T> getSpanAnnotationVector() {
		return spanAnnotationVector;
	}

}
