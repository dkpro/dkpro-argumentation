/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Jun 6, 2016
 *
 */
public interface SpanText {

	String getCoveredText();
	
	Span getSpan();

}
