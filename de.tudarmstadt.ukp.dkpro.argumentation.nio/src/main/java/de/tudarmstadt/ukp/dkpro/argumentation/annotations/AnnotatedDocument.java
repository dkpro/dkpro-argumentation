/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 10, 2016
 *
 */
public final class AnnotatedDocument<T extends SpanAnnotation> {

	private final SpanAnnotationGraph<T> annotations;

	private final String text;

	/**
	 *
	 */
	public AnnotatedDocument(final String text, final SpanAnnotationGraph<T> annotations) {
		this.text = text;
		this.annotations = annotations;
	}

	/**
	 * @return the annotations
	 */
	public SpanAnnotationGraph<T> getAnnotations() {
		return annotations;
	}

	public String getCoveredText(final SpanAnnotation spanAnnotation) {
		return text.substring(spanAnnotation.getBegin(), spanAnnotation.getEnd());
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

}
