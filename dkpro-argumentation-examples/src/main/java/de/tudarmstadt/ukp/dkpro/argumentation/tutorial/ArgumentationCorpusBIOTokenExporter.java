/*
 * Copyright 2017
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dkpro.argumentation.tutorial;

import org.dkpro.argumentation.io.writer.TokenTabBIOArgumentWriter;
import org.dkpro.argumentation.preprocessing.annotation.ArgumentTokenBIOAnnotator;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

/**
 * Reads the complete argumentation corpus from
 * https://www.ukp.tu-darmstadt.de/data/argumentation-mining/argument-annotated-user-generated-web-discourse/
 * and writes argument units (argument components, argument relations, implicit component, etc.)
 * into files split by tokens with their corresponding BIO tag.
 *
 * @author Ivan Habernal
 */
public class ArgumentationCorpusBIOTokenExporter
{
    public static void main(String[] args)
    {
        // TODO set this properly to "gold.data.toulmin" directory
        final String annotatedCorpusDir = args[0];

        // TODO set this to output directory
        final String outputDir = args[1];

        try {
            SimplePipeline.runPipeline(CollectionReaderFactory.createReaderDescription(
                    XmiReader.class,
                    XmiReader.PARAM_SOURCE_LOCATION, annotatedCorpusDir,
                    XmiReader.PARAM_PATTERNS, "[+]*.xmi",
                    XmiReader.PARAM_LENIENT, true
                    ),
                    // annotate with BIO tags
                    AnalysisEngineFactory.createEngineDescription(
                            ArgumentTokenBIOAnnotator.class,
                            ArgumentTokenBIOAnnotator.PARAM_LABEL_GRANULARITY,
                            ArgumentTokenBIOAnnotator.BIO
                    ),
                    // export to TXT files
                    AnalysisEngineFactory.createEngineDescription(
                            TokenTabBIOArgumentWriter.class,
                            TokenTabBIOArgumentWriter.PARAM_TARGET_LOCATION,
                            outputDir
                    )
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
