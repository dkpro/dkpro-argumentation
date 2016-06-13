/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations.uima;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.argumentation.annotations.Attribute;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.ImmutableSpan;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.ImmutableSpanText;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.ImmutableSpanTextLabel;
import de.tudarmstadt.ukp.dkpro.argumentation.types.Claim;

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

	private static Map<Attribute, Object> createAttrMap(final Annotation annotation) {
		final Map<Attribute, Object> result;
		if (annotation instanceof Claim) {
			result = Collections.singletonMap(Attribute.CATEGORY, ((Claim) annotation).getStance());
		} else {
			result = Collections.emptyMap();
		}
		return result;
	}

	private TextSpanAnnotationFactory() {
	}

	@Override
	public ImmutableSpanTextLabel apply(final Annotation annotation) {
		final ImmutableSpan span = new ImmutableSpan(annotation.getBegin(), annotation.getEnd());
		final ImmutableSpanText spanText = new ImmutableSpanText(span, annotation.getCoveredText());
		final Map<Attribute, Object> attrs = createAttrMap(annotation);
		return new ImmutableSpanTextLabel(spanText, annotation.getType().getShortName(), attrs);
	}

}
