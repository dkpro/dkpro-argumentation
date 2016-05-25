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

	private final String annotationType;

	private final int begin;

	private final int end;

	/**
	 *
	 */
	public SpanAnnotationException(final int begin, final int end, final String annotationType) {
		this.begin = begin;
		this.end = end;
		this.annotationType = annotationType;
	}

	/**
	 * @param message
	 */
	public SpanAnnotationException(final int begin, final int end, final String annotationType, final String message) {
		super(message);
		this.begin = begin;
		this.end = end;
		this.annotationType = annotationType;
	}

	public SpanAnnotationException(final int begin, final int end, final String annotationType, final String message,
			final Throwable cause) {
		super(message, cause);
		this.begin = begin;
		this.end = end;
		this.annotationType = annotationType;
	}

	public SpanAnnotationException(final int begin, final int end, final String annotationType, final String message,
			final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.begin = begin;
		this.end = end;
		this.annotationType = annotationType;
	}

	/**
	 * @param cause
	 */
	public SpanAnnotationException(final int begin, final int end, final String annotationType, final Throwable cause) {
		super(cause);
		this.begin = begin;
		this.end = end;
		this.annotationType = annotationType;
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
		if (annotationType == null) {
			if (other.annotationType != null) {
				return false;
			}
		} else if (!annotationType.equals(other.annotationType)) {
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
	 * @return the annotationType
	 */
	public String getAnnotationType() {
		return annotationType;
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
		result = prime * result + (annotationType == null ? 0 : annotationType.hashCode());
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
		return String.format(" (Annotation type \"%s\"; span [%d, %d])", annotationType, begin, end);
	}

}
