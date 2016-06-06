/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.nio.writer;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 2, 2016
 *
 */
public class SpanAnnotationException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -8088424492769878641L;

	private final int begin;

	private final int end;

	private final String label;

	/**
	 *
	 */
	public SpanAnnotationException(final int begin, final int end, final String label) {
		this.begin = begin;
		this.end = end;
		this.label = label;
	}

	/**
	 * @param message
	 */
	public SpanAnnotationException(final int begin, final int end, final String label, final String message) {
		super(message);
		this.begin = begin;
		this.end = end;
		this.label = label;
	}

	public SpanAnnotationException(final int begin, final int end, final String label, final String message,
			final Throwable cause) {
		super(message, cause);
		this.begin = begin;
		this.end = end;
		this.label = label;
	}

	public SpanAnnotationException(final int begin, final int end, final String label, final String message,
			final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.begin = begin;
		this.end = end;
		this.label = label;
	}

	/**
	 * @param cause
	 */
	public SpanAnnotationException(final int begin, final int end, final String label, final Throwable cause) {
		super(cause);
		this.begin = begin;
		this.end = end;
		this.label = label;
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
		if (!(obj instanceof SpanAnnotationException)) {
			return false;
		}
		final SpanAnnotationException other = (SpanAnnotationException) obj;
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
		if (end != other.end) {
			return false;
		}
		return true;
	}

	/**
	 * @return the label
	 */
	public String getAnnotationType() {
		return label;
	}

	/**
	 * @return the begin
	 */
	public int getBegin() {
		return begin;
	}

	/**
	 * @return the end
	 */
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
		final int prime = 31;
		int result = 1;
		result = prime * result + (label == null ? 0 : label.hashCode());
		result = prime * result + begin;
		result = prime * result + end;
		return result;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final String superString = super.toString();
		return superString + createMessageSuffix();
	}

	private String createMessageSuffix() {
		return String.format(" (Annotation label \"%s\"; span [%d, %d])", label, begin, end);
	}

}
