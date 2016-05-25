/*
 * Copyright 2014
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

package de.tudarmstadt.ukp.dkpro.argumentation.nio.writer.debug;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * Dumps all argument components in the {@code JCas} (similar to
 * {@link org.apache.uima.fit.component.CasDumpWriter}). If not specified, the
 * output goes to {@code System.out}.
 *
 * @author Ivan Habernal
 */
public final class DebugStreamDumpWriter extends JCasAnnotator_ImplBase {

	public static final String PARAM_ENCODING = "encoding";

	public static final String PARAM_INCLUDE_PROPERTIES = "includeProperties";

	public static final String PARAM_INCLUDE_RELATIONS = "includeRelations";

	/**
	 * Output file. If multiple CASes as processed, their contents are
	 * concatenated into this file. When this file is set to "-", the dump does
	 * to {@link System#out} (default).
	 */
	public static final String PARAM_OUTPUT_FILE = "outputFile";

	private PrintWriter out;

	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true, defaultValue = "-")
	private File outputFile;

	@ConfigurationParameter(name = PARAM_INCLUDE_PROPERTIES, mandatory = true, defaultValue = "true")
	boolean includeProperties;

	@ConfigurationParameter(name = PARAM_INCLUDE_RELATIONS, mandatory = true, defaultValue = "true")
	boolean includeRelations;

	@Override
	public void collectionProcessComplete() {
		IOUtils.closeQuietly(out);
		out = null;
	}

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);

		try {
			if (out == null) {
				if ("-".equals(outputFile.getName())) {
					// default to System.out
					out = new PrintWriter(new CloseShieldOutputStream(System.out));
				} else {
					if (outputFile.getParentFile() != null) {
						outputFile.getParentFile().mkdirs();
					}
					out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
				}
			}
		} catch (final IOException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(final JCas jCas) throws AnalysisEngineProcessException {
		DebugArgumentDumping.dump(out, jCas, includeProperties, includeRelations);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		IOUtils.closeQuietly(out);
		out = null;
		super.finalize();
	}
}
