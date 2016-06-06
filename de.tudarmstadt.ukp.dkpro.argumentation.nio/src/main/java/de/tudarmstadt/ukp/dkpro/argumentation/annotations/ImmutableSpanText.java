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
 * @since Jun 6, 2016
 *
 */
@JsonPropertyOrder({ ImmutableSpanText.PROPERTY_SPAN, ImmutableSpanText.PROPERTY_COVERED_TEXT })
public final class ImmutableSpanText implements SpanText, Serializable {

	public static final String PROPERTY_COVERED_TEXT = "coveredText";

	public static final String PROPERTY_SPAN = "span";

	/**
	 *
	 */
	private static final long serialVersionUID = -3916487992948159620L;

	private final String coveredText;

	private final transient int hashCode;

	private final ImmutableSpan span;

	/**
	 *
	 */
	@JsonCreator
	public ImmutableSpanText(@JsonProperty(PROPERTY_SPAN) final ImmutableSpan span,
			@JsonProperty(PROPERTY_COVERED_TEXT) final String coveredText) {
		this.span = span;
		this.coveredText = coveredText;

		hashCode = createHashCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	public int createHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (coveredText == null ? 0 : coveredText.hashCode());
		result = prime * result + (span == null ? 0 : span.hashCode());
		return result;
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
		if (!(obj instanceof ImmutableSpanText)) {
			return false;
		}
		final ImmutableSpanText other = (ImmutableSpanText) obj;
		if (coveredText == null) {
			if (other.coveredText != null) {
				return false;
			}
		} else if (!coveredText.equals(other.coveredText)) {
			return false;
		}
		if (span == null) {
			if (other.span != null) {
				return false;
			}
		} else if (!span.equals(other.span)) {
			return false;
		}
		return true;
	}

	@Override
	@JsonProperty(PROPERTY_COVERED_TEXT)
	public String getCoveredText() {
		return coveredText;
	}

	@Override
	@JsonProperty(PROPERTY_SPAN)
	public ImmutableSpan getSpan() {
		return span;
	}

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
		builder.append("ImmutableTextSpan [getSpan()=");
		builder.append(getSpan());
		builder.append(", getCoveredText()=");
		builder.append(getCoveredText());
		builder.append("]");
		return builder.toString();
	}

}
