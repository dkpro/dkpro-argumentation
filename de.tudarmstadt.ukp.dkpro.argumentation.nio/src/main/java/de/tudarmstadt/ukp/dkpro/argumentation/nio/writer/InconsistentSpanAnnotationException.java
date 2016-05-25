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
public final class InconsistentSpanAnnotationException extends SpanAnnotationException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6606066542189032991L;

	private final String coveredText;

	/**
	 *
	 */
	public InconsistentSpanAnnotationException(final int begin, final int end, final String annotationType,
			final String coveredText) {
		super(begin, end, annotationType);
		this.coveredText = coveredText;
	}

	/**
	 * @param message
	 */
	public InconsistentSpanAnnotationException(final int begin, final int end, final String annotationType,
			final String coveredText, final String message) {
		super(begin, end, annotationType, message);
		this.coveredText = coveredText;
	}

	public InconsistentSpanAnnotationException(final int begin, final int end, final String annotationType,
			final String coveredText, final String message, final Throwable cause) {
		super(begin, end, annotationType, message, cause);
		this.coveredText = coveredText;
	}

	public InconsistentSpanAnnotationException(final int begin, final int end, final String annotationType,
			final String coveredText, final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(begin, end, annotationType, message, cause, enableSuppression, writableStackTrace);
		this.coveredText = coveredText;
	}

	/**
	 * @param cause
	 */
	public InconsistentSpanAnnotationException(final int begin, final int end, final String annotationType,
			final String coveredText, final Throwable cause) {
		super(begin, end, annotationType, cause);
		this.coveredText = coveredText;
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
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof InconsistentSpanAnnotationException)) {
			return false;
		}
		final InconsistentSpanAnnotationException other = (InconsistentSpanAnnotationException) obj;
		if (coveredText == null) {
			if (other.coveredText != null) {
				return false;
			}
		} else if (!coveredText.equals(other.coveredText)) {
			return false;
		}
		return true;
	}

	/**
	 * @return the coveredText
	 */
	public String getCoveredText() {
		return coveredText;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (coveredText == null ? 0 : coveredText.hashCode());
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
		return String.format("; Covered text: \"%s\"", coveredText);
	}
}
