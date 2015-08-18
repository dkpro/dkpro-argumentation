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

import de.tudarmstadt.ukp.dkpro.argumentation.types.*;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * Dumps all argument components in the {@code JCas} (similar to {@link org.apache.uima.fit.component.CasDumpWriter}).
 * If not specified, the output goes to {@code System.out}.
 *
 * @author Ivan Habernal
 */
public class ArgumentDumpWriter
        extends JCasAnnotator_ImplBase
{

    /**
     * Output file. If multiple CASes as processed, their contents are concatenated into this file.
     * When this file is set to "-", the dump does to {@link System#out} (default).
     */
    public static final String PARAM_OUTPUT_FILE = "outputFile";

    @ConfigurationParameter(name = PARAM_OUTPUT_FILE, mandatory = true, defaultValue = "-")
    private File outputFile;

    public static final String PARAM_INCLUDE_PROPERTIES = "includeProperties";
    @ConfigurationParameter(name = PARAM_INCLUDE_PROPERTIES, mandatory = true,
            defaultValue = "true")
    boolean includeProperties;

    public static final String PARAM_INCLUDE_RELATIONS = "includeRelations";
    @ConfigurationParameter(name = PARAM_INCLUDE_RELATIONS, mandatory = true,
            defaultValue = "true")
    boolean includeRelations;

    private PrintWriter out;

    @Override
    public void initialize(UimaContext context)
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
        catch (IOException e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public void collectionProcessComplete()
    {
        IOUtils.closeQuietly(out);
        out = null;
    }

    /**
     * Dump argument components to String. Can be also used outside UIMA pipeline.
     *
     * @param jCas jcas
     * @return string dump
     */
    public static String dumpArguments(JCas jCas)
    {
        return dumpArguments(jCas, true, true);
    }

    /**
     * Dump argument components to String. Can be also used outside UIMA pipeline.
     *
     * @param jCas jcas
     * @return string dump
     */
    public static String dumpArguments(JCas jCas, boolean includeProperties, boolean includeRelations)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);

        String documentId = DocumentMetaData.get(jCas).getDocumentId();

        out.println("======== #" + documentId + " begin ==========================");

        Collection<ArgumentComponent> argumentComponents = JCasUtil
                .select(jCas, ArgumentComponent.class);

        out.println("----------------- ArgumentComponent -----------------");
        out.println("# of argument units: " + argumentComponents.size());

        for (ArgumentComponent argumentComponent : argumentComponents) {
            out.println(argumentUnitToString(argumentComponent));

            if (includeProperties) {
                out.println(formatProperties(argumentComponent));
            }

            if (argumentComponent instanceof Claim) {
                Claim claim = (Claim) argumentComponent;
                String stance = claim.getStance();

                if (stance != null) {
                    out.println("Stance: " + stance);
                }
            }
        }

        Collection<ArgumentRelation> argumentRelations = JCasUtil
                .select(jCas, ArgumentRelation.class);

        if (includeRelations) {
            out.println("----------------- ArgumentRelation -----------------");
            out.println("# of argument relations: " + argumentRelations.size());

            for (ArgumentRelation argumentRelation : argumentRelations) {
                out.println(argumentRelation.getType().getShortName());
                out.println("   source: " + argumentUnitToString(
                        argumentRelation.getSource()));
                out.println("   target: " + argumentUnitToString(
                        argumentRelation.getTarget()));

                if (includeProperties) {
                    out.println(formatProperties(argumentRelation));
                }
            }
        }

        out.println("======== #" + documentId + " end ==========================");

        try {
            return outputStream.toString("utf-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(JCas jCas)
            throws AnalysisEngineProcessException
    {
        out.println(dumpArguments(jCas, this.includeProperties, this.includeRelations));
    }

    /**
     * Formats argument unit to string with type, position, and text content
     *
     * @param argumentUnit argument unit
     * @return string
     */
    private static String argumentUnitToString(ArgumentUnit argumentUnit)
    {
        return String.format("%s [%d, %d] \"%s\"", argumentUnit.getType().getShortName(),
                argumentUnit.getBegin(), argumentUnit.getEnd(), argumentUnit.getCoveredText());
    }

    /**
     * Prints to string all non-null {@code properties} of the the given unit
     *
     * @param argumentUnit argument unit
     * @return string
     */
    private static String formatProperties(ArgumentUnit argumentUnit)
    {
        StringBuilder sb = new StringBuilder("Properties:\n");

        Properties properties = ArgumentUnitUtils.getProperties(argumentUnit);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (entry.getValue() != null) {
                sb.append("   ");
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
