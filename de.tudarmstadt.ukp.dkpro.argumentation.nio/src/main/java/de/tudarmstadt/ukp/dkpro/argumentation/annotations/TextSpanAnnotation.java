/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Apr 29, 2016
 *
 */
@JsonPropertyOrder({ TextSpanAnnotation.PROPERTY_BEGIN, TextSpanAnnotation.PROPERTY_END })
public final class TextSpanAnnotation implements Serializable, AnnotatedSpan {

	public static final String PROPERTY_LABEL = "label";

	public static final String PROPERTY_BEGIN = "begin";

	public static final String PROPERTY_COVERED_TEXT = "coveredText";

	public static final String PROPERTY_END = "end";

	/**
	 *
	 */
	private static final long serialVersionUID = -5564537471325147494L;

	private final String label;

	private final int begin;

	private final String coveredText;

	private final int end;

	private final transient int hashCode;

	/**
	 *
	 */
	@JsonCreator
	public TextSpanAnnotation(@JsonProperty(PROPERTY_BEGIN) final int begin, @JsonProperty(PROPERTY_END) final int end,
			@JsonProperty(PROPERTY_LABEL) final String label,
			@JsonProperty(PROPERTY_COVERED_TEXT) final String coveredText) {
		this.begin = begin;
		this.end = end;
		this.label = label;
		this.coveredText = coveredText;

		hashCode = createHashCode();
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
		if (!(obj instanceof TextSpanAnnotation)) {
			return false;
		}
		final TextSpanAnnotation other = (TextSpanAnnotation) obj;
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		if (begin != other.begin) {
			return false;
		}
		if (coveredText == null) {
			if (other.coveredText != null) {
				return false;
			}
		} else if (!coveredText.equals(other.coveredText)) {
			return false;
		}
		if (end != other.end) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.tudarmstadt.ukp.dkpro.argumentation.annotations.SpanAnnotation#
	 * getAnnotationType()
	 */
	@Override
	@JsonProperty(PROPERTY_LABEL)
	public String getLabel() {
		return label;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.tudarmstadt.ukp.dkpro.argumentation.annotations.SpanAnnotation#
	 * getBegin()
	 */
	@Override
	@JsonProperty(PROPERTY_BEGIN)
	public int getBegin() {
		return begin;
	}

	/**
	 * @return the coveredText
	 */
	@JsonProperty(PROPERTY_COVERED_TEXT)
	public String getCoveredText() {
		return coveredText;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.tudarmstadt.ukp.dkpro.argumentation.annotations.SpanAnnotation#getEnd(
	 * )
	 */
	@Override
	@JsonProperty(PROPERTY_END)
	public int getEnd() {
		return end;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("TextSpanAnnotation [begin=");
		builder.append(begin);
		builder.append(", end=");
		builder.append(end);
		builder.append(", label=");
		builder.append(label);
		builder.append(", coveredText=");
		builder.append(coveredText);
		builder.append("]");
		return builder.toString();
	}

	private int createHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (label == null ? 0 : label.hashCode());
		result = prime * result + begin;
		result = prime * result + (coveredText == null ? 0 : coveredText.hashCode());
		result = prime * result + end;
		return result;
	}

}
