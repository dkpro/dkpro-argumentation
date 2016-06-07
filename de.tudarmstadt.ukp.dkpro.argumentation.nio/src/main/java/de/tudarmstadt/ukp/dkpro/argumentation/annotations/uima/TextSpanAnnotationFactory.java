/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations.uima;

import java.util.function.Function;

import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.argumentation.annotations.ImmutableSpan;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.ImmutableSpanText;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.ImmutableSpanTextLabel;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 2, 2016
 *
 */
public final class TextSpanAnnotationFactory implements Function<Annotation, ImmutableSpanTextLabel> {

	/**
	 * {@link SingletonHolder} is loaded on the first execution of
	 * {@link TextSpanAnnotationFactory#getInstance()} or the first access to
	 * {@link SingletonHolder#INSTANCE}, not before.
	 *
	 * @author <a href="http://www.cs.umd.edu/~pugh/">Bill Pugh</a>
	 * @see <a href=
	 *      "https://en.wikipedia.org/wiki/Singleton_pattern#The_solution_of_Bill_Pugh">
	 *      https://en.wikipedia.org/wiki/Singleton_pattern#
	 *      The_solution_of_Bill_Pugh</a>
	 */
	private static final class SingletonHolder {
		/**
		 * A singleton instance of {@link TextSpanAnnotationFactory}.
		 */
		private static final TextSpanAnnotationFactory INSTANCE = new TextSpanAnnotationFactory();
	}

	public static TextSpanAnnotationFactory getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private TextSpanAnnotationFactory() {
	}

	@Override
	public ImmutableSpanTextLabel apply(final Annotation annotation) {
		final ImmutableSpan span = new ImmutableSpan(annotation.getBegin(), annotation.getEnd());
		final ImmutableSpanText spanText = new ImmutableSpanText(span, annotation.getCoveredText());
		return new ImmutableSpanTextLabel(spanText, annotation.getType().getShortName());
	}

}
