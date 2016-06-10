package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

public interface SpanTextLabel {

	/*
	 * (non-Javadoc)
	 *
	 * @see de.tudarmstadt.ukp.dkpro.argumentation.annotations.SpanAnnotation#
	 * getAnnotationType()
	 */
	String getLabel();

	ImmutableSpanText getSpanText();

}