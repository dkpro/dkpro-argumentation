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
@JsonPropertyOrder({ ImmutableSpan.PROPERTY_BEGIN, ImmutableSpan.PROPERTY_END })
public final class ImmutableSpan implements Span, Serializable {

	public static final String PROPERTY_BEGIN = "begin";

	public static final String PROPERTY_END = "end";

	/**
	 *
	 */
	private static final long serialVersionUID = 5872170320837177233L;

	private final int begin;

	private final int end;

	private final transient int hashCode;

	/**
	 *
	 */
	@JsonCreator
	public ImmutableSpan(@JsonProperty(PROPERTY_BEGIN) final int begin, @JsonProperty(PROPERTY_END) final int end) {
		this.begin = begin;
		this.end = end;

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
		result = prime * result + begin;
		result = prime * result + end;
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
		if (!(obj instanceof ImmutableSpan)) {
			return false;
		}
		final ImmutableSpan other = (ImmutableSpan) obj;
		if (begin != other.begin) {
			return false;
		}
		if (end != other.end) {
			return false;
		}
		return true;
	}

	@Override
	@JsonProperty(PROPERTY_BEGIN)
	public int getBegin() {
		return begin;
	}

	@Override
	@JsonProperty(PROPERTY_END)
	public int getEnd() {
		return end;
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
		builder.append("ImmutableSpan [getBegin()=");
		builder.append(getBegin());
		builder.append(", getEnd()=");
		builder.append(getEnd());
		builder.append("]");
		return builder.toString();
	}

}
