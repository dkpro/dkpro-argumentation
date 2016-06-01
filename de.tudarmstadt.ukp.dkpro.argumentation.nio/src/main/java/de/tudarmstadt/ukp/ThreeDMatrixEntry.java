/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since May 30, 2016
 *
 */
public interface ThreeDMatrixEntry<K, V> {

	int get1DIdx();

	int get2DIdx();

	K get3DIdx();

	V getValue();

}
