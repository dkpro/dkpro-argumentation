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
public final class InconsistentSpanAnnotationException extends SpanAnnotationException {

	/**
	 *
	 */
	private static final long serialVersionUID = 6606066542189032991L;

	private final String coveredText;

	/**
	 *
	 */
	public InconsistentSpanAnnotationException(final int begin, final int end, final String label,
			final String coveredText) {
		super(begin, end, label);
		this.coveredText = coveredText;
	}

	/**
	 * @param message
	 */
	public InconsistentSpanAnnotationException(final int begin, final int end, final String label,
			final String coveredText, final String message) {
		super(begin, end, label, message);
		this.coveredText = coveredText;
	}

	public InconsistentSpanAnnotationException(final int begin, final int end, final String label,
			final String coveredText, final String message, final Throwable cause) {
		super(begin, end, label, message, cause);
		this.coveredText = coveredText;
	}

	public InconsistentSpanAnnotationException(final int begin, final int end, final String label,
			final String coveredText, final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(begin, end, label, message, cause, enableSuppression, writableStackTrace);
		this.coveredText = coveredText;
	}

	/**
	 * @param cause
	 */
	public InconsistentSpanAnnotationException(final int begin, final int end, final String label,
			final String coveredText, final Throwable cause) {
		super(begin, end, label, cause);
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
