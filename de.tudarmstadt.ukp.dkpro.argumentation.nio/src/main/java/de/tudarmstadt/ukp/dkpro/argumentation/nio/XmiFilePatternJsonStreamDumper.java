/*
 * Copyright 2016
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
package de.tudarmstadt.ukp.dkpro.argumentation.nio;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import de.tudarmstadt.ukp.dkpro.argumentation.nio.writer.json.JsonStreamDumpWriter;
import de.tudarmstadt.ukp.dkpro.core.api.io.ResourceCollectionReaderBase;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;

/**
 * Derived from <a href=
 * "https://github.com/dkpro/dkpro-argumentation/blob/master/de.tudarmstadt.ukp.dkpro.argumentation.examples/src/main/java/tutorial/ArgumentationCorpusDebugger.java">
 * <code>de.tudarmstadt.ukp.dkpro.argumentation.tutorial.ArgumentationCorpusDebugger</code> </a>
 * because the Maven package structure for getting that class is very bad.
 *
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Apr 28, 2016
 *
 */
public final class XmiFilePatternJsonStreamDumper
    implements Callable<Void>
{

    private enum Parameter
        implements Supplier<Option>
    {
        SOURCE_LOCATION("s")
        {
            @Override
            public Option get()
            {
                return Option.builder(paramName).required().hasArg().argName("location")
                        .desc("Location from which the input is read.").longOpt("source").build();
            }
        },
        TARGET_LOCATION("t")
        {
            @Override
            public Option get()
            {
                return Option.builder(paramName).hasArg().argName("location")
                        .desc("Location to which the output is written.").longOpt("target").build();
            }
        };

        private static Options createOptions()
        {
            final Options result = new Options();

            for (final Parameter param : Parameter.values()) {
                result.addOption(param.get());
            }

            return result;
        }

        final String paramName;

        private Parameter(final String paramName)
        {
            this.paramName = paramName;
        }

    }

    private static final Log LOG = LogFactory.getLog(XmiFilePatternJsonStreamDumper.class);

    public static void main(final String[] args)
        throws ParseException, UIMAException, IOException
    {
        // TODO: It would be nice to be able to read from standard input but it
        // seems that ResourceCollectionReaderBase doesn't facilitate this
        final CommandLineParser parser = new DefaultParser();
        final Options opts = Parameter.createOptions();
        try {
            final CommandLine commandLine = parser.parse(opts, args);
            final String sourceLocation = commandLine
                    .getOptionValue(Parameter.SOURCE_LOCATION.paramName);
            LOG.info(String.format("Source location is \"%s\".", sourceLocation));
            final String targetLocation = commandLine
                    .getOptionValue(Parameter.TARGET_LOCATION.paramName);
            LOG.info(String.format("Target location is \"%s\".", targetLocation));
            new XmiFilePatternJsonStreamDumper(JsonStreamDumpWriter.class, sourceLocation,
                    targetLocation).call();
        }
        catch (final ParseException e) {
            printHelp(opts);
            throw e;
        }
    }

    private static void printHelp(final Options opts)
    {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(XmiFilePatternJsonStreamDumper.class.getName(), opts);
    }

    private final Class<? extends AnalysisComponent> analysisComponentClass;

    private final String sourceLocation;

    private final String targetLocation;

    public XmiFilePatternJsonStreamDumper(
            final Class<? extends AnalysisComponent> analysisComponentClass,
            final String sourceLocation, final String targetLocation)
    {
        this.analysisComponentClass = analysisComponentClass;
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
    }

    @Override
    public Void call()
        throws UIMAException, IOException
    {
        final String includePattern = ResourceCollectionReaderBase.INCLUDE_PREFIX + "*.xmi";
        final CollectionReaderDescription xmiReaderDesc = CollectionReaderFactory
                .createReaderDescription(XmiReader.class,
                        ResourceCollectionReaderBase.PARAM_SOURCE_LOCATION, sourceLocation,
                        ResourceCollectionReaderBase.PARAM_PATTERNS, includePattern,
                        XmiReader.PARAM_LENIENT, false);
        final AnalysisEngineDescription argumentDumpEngineDesc = AnalysisEngineFactory
                .createEngineDescription(analysisComponentClass,
                        JsonStreamDumpWriter.PARAM_OUTPUT_FILE, targetLocation);
        SimplePipeline.runPipeline(xmiReaderDesc, argumentDumpEngineDesc);
        return null;
    }
}
