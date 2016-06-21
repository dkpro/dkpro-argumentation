/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class representing a document annotated with {@link SpanTextLabel} objects.
 *
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 10, 2016
 *
 */
public final class AnnotatedDocument<T extends SpanTextLabel> {

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
		if (!(obj instanceof AnnotatedDocument)) {
			return false;
		}
		final AnnotatedDocument<?> other = (AnnotatedDocument<?>) obj;
		if (annotations == null) {
			if (other.annotations != null) {
				return false;
			}
		} else if (!annotations.equals(other.annotations)) {
			return false;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		} else if (!text.equals(other.text)) {
			return false;
		}
		return true;
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

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (annotations == null ? 0 : annotations.hashCode());
		result = prime * result + (text == null ? 0 : text.hashCode());
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
		builder.append("AnnotatedDocument [text=");
		builder.append(text);
		builder.append(", annotations=");
		builder.append(annotations);
		builder.append("]");
		return builder.toString();
	}

}
