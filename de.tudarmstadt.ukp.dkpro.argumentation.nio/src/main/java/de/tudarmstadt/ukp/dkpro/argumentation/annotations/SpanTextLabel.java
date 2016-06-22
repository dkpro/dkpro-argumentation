package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

import java.util.Map;

public interface SpanTextLabel {

	/**
	 * @return the attributes
	 */
	Map<Attribute, Object> getAttributes();

	/*
	 * (non-Javadoc)
	 *
	 * @see de.tudarmstadt.ukp.dkpro.argumentation.annotations.SpanAnnotation#
	 * getAnnotationType()
	 */
	String getLabel();

	SpanText getSpanText();

}