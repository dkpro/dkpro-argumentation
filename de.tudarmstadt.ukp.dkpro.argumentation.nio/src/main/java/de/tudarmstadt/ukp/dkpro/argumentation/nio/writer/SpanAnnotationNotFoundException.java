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
public final class SpanAnnotationNotFoundException extends SpanAnnotationException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3102176978651248772L;

	public SpanAnnotationNotFoundException(final int begin, final int end, final String annotationType) {
		super(begin, end, annotationType);
	}

	public SpanAnnotationNotFoundException(final int begin, final int end, final String annotationType,
			final String message) {
		super(begin, end, annotationType, message);
	}

	public SpanAnnotationNotFoundException(final int begin, final int end, final String annotationType,
			final String message, final Throwable cause) {
		super(begin, end, annotationType, message, cause);
	}

	public SpanAnnotationNotFoundException(final int begin, final int end, final String annotationType,
			final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(begin, end, annotationType, message, cause, enableSuppression, writableStackTrace);
	}

	public SpanAnnotationNotFoundException(final int begin, final int end, final String annotationType,
			final Throwable cause) {
		super(begin, end, annotationType, cause);
	}

}
