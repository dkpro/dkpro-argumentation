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

package de.tudarmstadt.ukp.dkpro.argumentation.io.writer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentRelation;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnitUtils;
import de.tudarmstadt.ukp.dkpro.argumentation.types.Claim;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/**
 * Dumps all argument components in the {@code JCas} (similar to
 * {@link org.apache.uima.fit.component.CasDumpWriter}). If not specified, the output goes to
 * {@code System.out}.
 *
 * @author Ivan Habernal
 */
public class ArgumentDumpWriter
    extends JCasAnnotator_ImplBase
{

    public static final String PARAM_INCLUDE_PROPERTIES = "includeProperties";

    public static final String PARAM_INCLUDE_RELATIONS = "includeRelations";

    /**
     * Output file. If multiple CASes as processed, their contents are concatenated into this file.
     * When this file is set to "-", the dump does to {@link System#out} (default).
     */
    public static final String PARAM_OUTPUT_FILE = "outputFile";
    /**
     * Formats argument unit to string with type, position, and text content
     *
     * @param argumentUnit
     *            argument unit
     * @return string
     */
    public static String argumentUnitToString(final ArgumentUnit argumentUnit)
    {
        return String.format("%s [%d, %d] \"%s\"", argumentUnit.getType().getShortName(),
                argumentUnit.getBegin(), argumentUnit.getEnd(), argumentUnit.getCoveredText());
    }

    /**
     * Dump argument components to String. Can be also used outside UIMA pipeline.
     *
     * @param jCas
     *            jcas
     * @return string dump
     */
    public static String dumpArguments(final JCas jCas)
    {
        return dumpArguments(jCas, true, true);
    }
    /**
     * Dump argument components to String. Can be also used outside UIMA pipeline.
     *
     * @param jCas
     *            jcas
     * @param includeProperties
     *            Include argument component properties
     * @param includeRelations
     *            Include argument relations
     * @return string dump
     */
    public static String dumpArguments(final JCas jCas, final boolean includeProperties,
            final boolean includeRelations)
    {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final PrintStream out = new PrintStream(outputStream);

        final String documentId = DocumentMetaData.get(jCas).getDocumentId();

        out.println("======== #" + documentId + " begin ==========================");

        final Collection<ArgumentComponent> argumentComponents = JCasUtil.select(jCas,
                ArgumentComponent.class);

        out.println("----------------- ArgumentComponent -----------------");
        out.println("# of argument units: " + argumentComponents.size());

        for (final ArgumentComponent argumentComponent : argumentComponents) {
            out.println(argumentUnitToString(argumentComponent));

            if (includeProperties) {
                out.println(formatProperties(argumentComponent));
            }

            if (argumentComponent instanceof Claim) {
                final Claim claim = (Claim) argumentComponent;
                final String stance = claim.getStance();

                if (stance != null) {
                    out.println("Stance: " + stance);
                }
            }
        }

        final Collection<ArgumentRelation> argumentRelations = JCasUtil.select(jCas,
                ArgumentRelation.class);

        if (includeRelations) {
            out.println("----------------- ArgumentRelation -----------------");
            out.println("# of argument relations: " + argumentRelations.size());

            for (final ArgumentRelation argumentRelation : argumentRelations) {
                out.println(argumentRelation.getType().getShortName());
                out.println("   source: " + argumentUnitToString(argumentRelation.getSource()));
                out.println("   target: " + argumentUnitToString(argumentRelation.getTarget()));

                if (includeProperties) {
                    out.println(formatProperties(argumentRelation));
                }
            }
        }

        out.println("======== #" + documentId + " end ==========================");

        try {
            return outputStream.toString("utf-8");
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints to string all non-null {@code properties} of the the given unit
     *
     * @param argumentUnit
     *            argument unit
     * @return string
     */
    private static String formatProperties(final ArgumentUnit argumentUnit)
    {
        final StringBuilder sb = new StringBuilder("Properties:" + System.lineSeparator());

        final Properties properties = ArgumentUnitUtils.getProperties(argumentUnit);
        for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getValue() != null) {
                sb.append("   ");
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
                sb.append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    private PrintWriter out;

    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true, defaultValue = "-")
    private File outputFile;

    @ConfigurationParameter(name = PARAM_INCLUDE_PROPERTIES, mandatory = true, defaultValue = "true")
    boolean includeProperties;

    @ConfigurationParameter(name = PARAM_INCLUDE_RELATIONS, mandatory = true, defaultValue = "true")
    boolean includeRelations;

    @Override
    public void collectionProcessComplete()
    {
        IOUtils.closeQuietly(out);
        out = null;
    }

    @Override
    public void initialize(final UimaContext context)
        throws ResourceInitializationException
    {
        super.initialize(context);

        try {
            if (out == null) {
                if ("-".equals(outputFile.getName())) {
                    // default to System.out
                    out = new PrintWriter(new CloseShieldOutputStream(System.out));
                }
                else {
                    if (outputFile.getParentFile() != null) {
                        outputFile.getParentFile().mkdirs();
                    }
                    out = new PrintWriter(
                            new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));
                }
            }
        }
        catch (final IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public void process(final JCas jCas)
        throws AnalysisEngineProcessException
    {
        out.println(dumpArguments(jCas, includeProperties, includeRelations));
    }
}
