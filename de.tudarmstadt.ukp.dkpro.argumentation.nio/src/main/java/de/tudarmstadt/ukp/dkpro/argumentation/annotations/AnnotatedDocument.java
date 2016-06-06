/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 10, 2016
 *
 */
public final class AnnotatedDocument<T extends AnnotatedSpan> {

	public static final String PROPERTY_ANNOTATIONS = "annotations";

	public static final String PROPERTY_TEXT = "text";

	private final SpanAnnotationGraph<T> annotations;

	private final String text;

	/**
	 *
	 */
	@JsonCreator
	public AnnotatedDocument(@JsonProperty(PROPERTY_TEXT) final String text,
			@JsonProperty(PROPERTY_ANNOTATIONS) final SpanAnnotationGraph<T> annotations) {
		this.text = text;
		this.annotations = annotations;
	}

	/**
	 * @return the annotations
	 */
	@JsonProperty(PROPERTY_ANNOTATIONS)
	public SpanAnnotationGraph<T> getAnnotations() {
		return annotations;
	}

	public String getCoveredText(final Span spanAnnotation) {
		return text.substring(spanAnnotation.getBegin(), spanAnnotation.getEnd());
	}

	/**
	 * @return the text
	 */
	@JsonProperty(PROPERTY_TEXT)
	public String getText() {
		return text;
	}

}
