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
public final class SpanAnnotationNotFoundException extends SpanAnnotationException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3102176978651248772L;

	public SpanAnnotationNotFoundException(final int begin, final int end, final String label) {
		super(begin, end, label);
	}

	public SpanAnnotationNotFoundException(final int begin, final int end, final String label, final String message) {
		super(begin, end, label, message);
	}

	public SpanAnnotationNotFoundException(final int begin, final int end, final String label, final String message,
			final Throwable cause) {
		super(begin, end, label, message, cause);
	}

	public SpanAnnotationNotFoundException(final int begin, final int end, final String label, final String message,
			final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(begin, end, label, message, cause, enableSuppression, writableStackTrace);
	}

	public SpanAnnotationNotFoundException(final int begin, final int end, final String label, final Throwable cause) {
		super(begin, end, label, cause);
	}

}
