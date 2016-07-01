/*
 * Copyright 2015
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

package de.tudarmstadt.ukp.dkpro.argumentation.tutorial;

import de.tudarmstadt.ukp.dkpro.argumentation.io.writer.ArgumentDumpWriter;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

/**
 * Reads the complete argumentation corpus from
 * https://www.ukp.tu-darmstadt.de/data/argumentation-mining/argument-annotated-user-generated-web-discourse/
 * and prints argument units (argument components, argument relations, implicit component, etc.)
 *
 * @author Ivan Habernal
 */
public class ArgumentationCorpusDebugger
{
    public static void main(String[] args)
    {
        // TODO set this properly to "gold.data.toulmin" directory
        final String annotatedCorpusDir = "data/gold.data.toulmin/";

        try {
            SimplePipeline.runPipeline(CollectionReaderFactory.createReaderDescription(
                            XmiReader.class,
                            XmiReader.PARAM_SOURCE_LOCATION, annotatedCorpusDir,
                            XmiReader.PARAM_PATTERNS, "[+]*.xmi",
                            XmiReader.PARAM_LENIENT, true
                    ),
                    AnalysisEngineFactory.createEngineDescription(
                            ArgumentDumpWriter.class
                    )
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
