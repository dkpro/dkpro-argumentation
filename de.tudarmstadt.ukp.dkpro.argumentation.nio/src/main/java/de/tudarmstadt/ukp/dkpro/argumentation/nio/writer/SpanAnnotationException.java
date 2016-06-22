/*
 * Copyright 2016
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
