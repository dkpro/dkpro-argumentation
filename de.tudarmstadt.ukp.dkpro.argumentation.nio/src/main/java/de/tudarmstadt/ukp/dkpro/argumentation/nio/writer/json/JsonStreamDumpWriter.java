/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.nio.writer.json;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import de.tudarmstadt.ukp.dkpro.argumentation.annotations.AnnotatedDocument;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.ImmutableSpanTextLabel;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.SpanAnnotationGraph;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/**
 * TODO: Implement usage of {@link JsonStreamDumpWriter#PARAM_OUTPUT_FILE} param
 * in calling classes
 *
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Apr 29, 2016
 *
 */
public final class JsonStreamDumpWriter extends JCasAnnotator_ImplBase {

	private class DirectoryDumper implements Callable<Void> {

		@Override
		public Void call() throws IOException {
			for (final Entry<String, AnnotatedDocument<ImmutableSpanTextLabel>> documentAnnotation : documentAnnotations
					.entrySet()) {
				final String docId = documentAnnotation.getKey();
				final String docFilename = docId + ".json";
				final File docFile = new File(outputPath, docFilename);
				try (PrintWriter outputWriter = new PrintWriter(new FileWriter(docFile, false));) {
					OBJECT_MAPPER.writeValue(outputWriter, documentAnnotation.getValue());
				}
			}
			return null;
		}

	}

	private class FileDumper implements Callable<Void> {
		@Override
		public Void call() throws IOException {
			try (PrintWriter outputWriter = createOutputWriter(outputPath)) {
				OBJECT_MAPPER.writeValue(outputWriter, documentAnnotations);
			}
			return null;
		}

		private PrintWriter createOutputWriter(final File outputPath) throws IOException {
			// default to System.out
			return outputPath == null ? new PrintWriter(System.out)
					: new PrintWriter(new FileWriter(outputPath, false));
		}

	}

	/**
	 * The parameter referring to the path to write the processed results to.
	 */
	public static final String PARAM_OUTPUT_FILE = "outputPath";

	private static final Log LOG = LogFactory.getLog(JsonStreamDumpWriter.class);

	private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

	/**
	 * TODO: Make this configurable via parameters passed to annotators of this
	 * class
	 */
	private static ObjectMapper createObjectMapper() {
		final ObjectMapper result = new ObjectMapper();
		result.enable(SerializationFeature.INDENT_OUTPUT);
		return result;
	}

	private Map<String, AnnotatedDocument<ImmutableSpanTextLabel>> documentAnnotations;

	@ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = false, description = "The path to write the processed results to.")
	private File outputPath;

	private Callable<Void> pathWriter;

	@Override
	public void collectionProcessComplete() throws AnalysisEngineProcessException {
		try {
			pathWriter.call();
		} catch (final Exception e) {
			throw new AnalysisEngineProcessException(e);
		}
		documentAnnotations = null;
	}

	@Override
	public void initialize(final UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		if (outputPath != null && outputPath.isDirectory()) {
			pathWriter = new DirectoryDumper();
		} else {
			pathWriter = new FileDumper();
		}

		// TODO: Make initial capacity configurable
		documentAnnotations = new HashMap<>();
	}

	@Override
	public void process(final JCas jCas) throws AnalysisEngineProcessException {
		final String documentId = DocumentMetaData.get(jCas).getDocumentId();
		LOG.info(String.format("Processing document \"%s\".", documentId));
		final JCasTextSpanAnnotationGraphFactory converter = new JCasTextSpanAnnotationGraphFactory();
		final SpanAnnotationGraph<ImmutableSpanTextLabel> spanAnnotations = converter.apply(jCas);
		final AnnotatedDocument<ImmutableSpanTextLabel> doc = new AnnotatedDocument<>(
				jCas.getDocumentText(), spanAnnotations);

		documentAnnotations.put(documentId, doc);

	}
}
