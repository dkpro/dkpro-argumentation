package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

public interface SpanAnnotation {

	/**
	 * @return the annotationType
	 */
	String getAnnotationType();

	/**
	 * @return the begin
	 */
	int getBegin();

	/**
	 * @return the end
	 */
	int getEnd();

}