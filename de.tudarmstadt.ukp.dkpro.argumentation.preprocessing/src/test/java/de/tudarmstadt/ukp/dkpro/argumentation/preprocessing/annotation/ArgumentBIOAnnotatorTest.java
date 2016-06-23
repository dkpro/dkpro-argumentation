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

package de.tudarmstadt.ukp.dkpro.argumentation.preprocessing.annotation;

import de.tudarmstadt.ukp.dkpro.argumentation.types.*;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ArgumentBIOAnnotatorTest
{
    private JCas jCas;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp()
            throws Exception
    {
        // PB - premise begin, PI - premise in
        jCas = JCasFactory.createJCas();
        jCas.setDocumentText("S1O S1PB S1PI. S2PI S2O. S3O.");
        jCas.setDocumentLanguage("en");
        DocumentMetaData metaData = new DocumentMetaData(jCas);
        metaData.addToIndexes();

        SimplePipeline.runPipeline(jCas,
                AnalysisEngineFactory.createEngineDescription(BreakIteratorSegmenter.class)
        );

        Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
        assertEquals(3, sentences.size());

        List<Token> tokens = new ArrayList<Token>(JCasUtil.select(jCas, Token.class));

        ArgumentComponent premise1 = new Premise(jCas);
        premise1.setBegin(tokens.get(1).getBegin());
        premise1.setEnd(tokens.get(4).getEnd());
        premise1.addToIndexes();

        ArgumentComponent selectedPremise = JCasUtil.select(jCas, ArgumentComponent.class)
                .iterator().next();
        assertEquals("S1PB S1PI. S2PI", selectedPremise.getCoveredText());

    }

    @Test
    public void testOutputLabelAnnotator()
            throws Exception
    {

        SimplePipeline.runPipeline(jCas,
                AnalysisEngineFactory.createEngineDescription(ArgumentTokenBIOAnnotator.class)
        );

        List<BIOTokenArgumentAnnotation> argumentSequenceLabels = new ArrayList<BIOTokenArgumentAnnotation>(
                JCasUtil.select(jCas, BIOTokenArgumentAnnotation.class));
        //        for (ArgumentSequenceLabel label : argumentSequenceLabels) {
        //            System.out.print(label.getTag() + " ");
        //        }

        assertEquals("O", argumentSequenceLabels.get(0).getTag());
        assertEquals("Premise-B", argumentSequenceLabels.get(1).getTag());
        assertEquals("Premise-I", argumentSequenceLabels.get(2).getTag());
        assertEquals("Premise-I", argumentSequenceLabels.get(3).getTag());
        assertEquals("Premise-I", argumentSequenceLabels.get(4).getTag());
        assertEquals("O", argumentSequenceLabels.get(5).getTag());
        assertEquals("O", argumentSequenceLabels.get(6).getTag());
        assertEquals("O", argumentSequenceLabels.get(7).getTag());
        assertEquals("O", argumentSequenceLabels.get(8).getTag());
    }

    @Test
    public void testSimplifiedTokenBIO()
            throws Exception
    {
        // this must fail, it needs to also be annotated by ArgumentTokenBIOAnnotator
        exception.expect(AnalysisEngineProcessException.class);
        SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngineDescription(
                ArgumentSimplifiedTokenBIOAnnotator.class));

        SimplePipeline.runPipeline(jCas,
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentTokenBIOAnnotator.class),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedTokenBIOAnnotator.class)
        );

        List<BIOSimplifiedTokenArgumentAnnotation> labelsPredicted = new ArrayList<BIOSimplifiedTokenArgumentAnnotation>(
                JCasUtil.select(jCas, BIOSimplifiedTokenArgumentAnnotation.class));
        //        for (ArgumentSequenceLabel label : argumentSequenceLabels) {
        //            System.out.print(label.getTag() + " ");
        //        }

        assertEquals("Premise-B", labelsPredicted.get(0).getTag());
        assertEquals("Premise-I", labelsPredicted.get(1).getTag());
        assertEquals("Premise-I", labelsPredicted.get(2).getTag());
        assertEquals("Premise-I", labelsPredicted.get(3).getTag());
        assertEquals("Premise-I", labelsPredicted.get(4).getTag());
        assertEquals("Premise-I", labelsPredicted.get(5).getTag());
        assertEquals("Premise-I", labelsPredicted.get(6).getTag());
        assertEquals("O", labelsPredicted.get(7).getTag());
        assertEquals("O", labelsPredicted.get(8).getTag());
    }

    @Test
    public void testSimplifiedTokenIO()
            throws Exception
    {
        SimplePipeline.runPipeline(jCas,
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentTokenBIOAnnotator.class),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedTokenBIOAnnotator.class,
                        ArgumentSimplifiedTokenBIOAnnotator.PARAM_LABEL_GRANULARITY,
                        ArgumentBIOAnnotator.IO)
        );

        List<BIOSimplifiedTokenArgumentAnnotation> labelsPredicted = new ArrayList<BIOSimplifiedTokenArgumentAnnotation>(
                JCasUtil.select(jCas, BIOSimplifiedTokenArgumentAnnotation.class));

        assertEquals("Premise-I", labelsPredicted.get(0).getTag());
        assertEquals("Premise-I", labelsPredicted.get(1).getTag());
        assertEquals("Premise-I", labelsPredicted.get(2).getTag());
        assertEquals("Premise-I", labelsPredicted.get(3).getTag());
        assertEquals("Premise-I", labelsPredicted.get(4).getTag());
        assertEquals("Premise-I", labelsPredicted.get(5).getTag());
        assertEquals("Premise-I", labelsPredicted.get(6).getTag());
        assertEquals("O", labelsPredicted.get(7).getTag());
        assertEquals("O", labelsPredicted.get(8).getTag());
    }

    @Test
    public void testSimplifiedSentenceAnnotationBIO()
            throws Exception
    {
        SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngineDescription(
                ArgumentSimplifiedSentenceBIOAnnotator.class));

        List<BIOSimplifiedSentenceArgumentAnnotation> sentences =
                new ArrayList<BIOSimplifiedSentenceArgumentAnnotation>(
                        JCasUtil.select(jCas, BIOSimplifiedSentenceArgumentAnnotation.class));

        assertEquals(3, sentences.size());
        assertEquals("Premise-B", sentences.get(0).getTag());
        assertEquals("Premise-I", sentences.get(1).getTag());
        assertEquals("O", sentences.get(2).getTag());
    }

    @Test
    public void testSimplifiedSentenceAnnotationBIOEachIsB()
            throws Exception
    {
        SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngineDescription(
                ArgumentSimplifiedSentenceBIOAnnotator.class,
                ArgumentSimplifiedSentenceBIOAnnotator.PARAM_START_EACH_SENTENCE_WITH_B, true
        ));

        List<BIOSimplifiedSentenceArgumentAnnotation> sentences =
                new ArrayList<BIOSimplifiedSentenceArgumentAnnotation>(
                        JCasUtil.select(jCas, BIOSimplifiedSentenceArgumentAnnotation.class));

        assertEquals(3, sentences.size());
        assertEquals("Premise-B", sentences.get(0).getTag());
        assertEquals("Premise-B", sentences.get(1).getTag());
        assertEquals("O", sentences.get(2).getTag());
    }

    @Test
    public void testSimplifiedSentenceAnnotationIO()
            throws Exception
    {
        SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngineDescription(
                ArgumentSimplifiedSentenceBIOAnnotator.class,
                ArgumentSimplifiedTokenBIOAnnotator.PARAM_LABEL_GRANULARITY,
                ArgumentBIOAnnotator.IO));

        List<BIOSimplifiedSentenceArgumentAnnotation> sentences =
                new ArrayList<BIOSimplifiedSentenceArgumentAnnotation>(
                        JCasUtil.select(jCas, BIOSimplifiedSentenceArgumentAnnotation.class));

        assertEquals(3, sentences.size());
        assertEquals("Premise-I", sentences.get(0).getTag());
        assertEquals("Premise-I", sentences.get(1).getTag());
        assertEquals("O", sentences.get(2).getTag());
    }

    @Test
    public void testSimplifiedTokenPipelineDependencies()
            throws Exception
    {
        // this must fail, it needs to also be annotated by ArgumentTokenBIOAnnotator
        exception.expect(AnalysisEngineProcessException.class);
        SimplePipeline.runPipeline(jCas, AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedTokenBIOAnnotator.class,
                        ArgumentSimplifiedTokenBIOAnnotator.PARAM_RECREATE_FROM_SIMPLIFIED_SENTENCE_ANNOTATIONS,
                        true)
        );

        // this must fail, it needs to also be annotated by ArgumentTokenBIOAnnotator
        exception.expect(AnalysisEngineProcessException.class);
        SimplePipeline.runPipeline(jCas,
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedTokenBIOAnnotator.class),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedTokenBIOAnnotator.class,
                        ArgumentSimplifiedTokenBIOAnnotator.PARAM_RECREATE_FROM_SIMPLIFIED_SENTENCE_ANNOTATIONS,
                        true)
        );
    }

    @Test
    public void testSimplifiedTokenFromSentenceBIO()
            throws Exception
    {
        SimplePipeline.runPipeline(jCas,
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentTokenBIOAnnotator.class),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedSentenceBIOAnnotator.class),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedTokenBIOAnnotator.class,
                        ArgumentSimplifiedTokenBIOAnnotator.PARAM_RECREATE_FROM_SIMPLIFIED_SENTENCE_ANNOTATIONS,
                        true)
        );

        List<BIOSimplifiedTokenArgumentAnnotation> labelsPredicted = new ArrayList<BIOSimplifiedTokenArgumentAnnotation>(
                JCasUtil.select(jCas, BIOSimplifiedTokenArgumentAnnotation.class));

        assertEquals("Premise-B", labelsPredicted.get(0).getTag());
        assertEquals("Premise-I", labelsPredicted.get(1).getTag());
        assertEquals("Premise-I", labelsPredicted.get(2).getTag());
        assertEquals("Premise-I", labelsPredicted.get(3).getTag());
        assertEquals("Premise-I", labelsPredicted.get(4).getTag());
        assertEquals("Premise-I", labelsPredicted.get(5).getTag());
        assertEquals("Premise-I", labelsPredicted.get(6).getTag());
        assertEquals("O", labelsPredicted.get(7).getTag());
        assertEquals("O", labelsPredicted.get(8).getTag());
    }

    @Test
    public void testSimplifiedTokenFromSentenceIO()
            throws Exception
    {
        SimplePipeline.runPipeline(jCas,
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentTokenBIOAnnotator.class),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedSentenceBIOAnnotator.class),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedTokenBIOAnnotator.class,
                        ArgumentSimplifiedTokenBIOAnnotator.PARAM_RECREATE_FROM_SIMPLIFIED_SENTENCE_ANNOTATIONS,
                        true,
                        ArgumentBIOAnnotator.PARAM_LABEL_GRANULARITY, ArgumentBIOAnnotator.IO)
        );

        List<BIOSimplifiedTokenArgumentAnnotation> labelsPredicted = new ArrayList<BIOSimplifiedTokenArgumentAnnotation>(
                JCasUtil.select(jCas, BIOSimplifiedTokenArgumentAnnotation.class));

        assertEquals("Premise-I", labelsPredicted.get(0).getTag());
        assertEquals("Premise-I", labelsPredicted.get(1).getTag());
        assertEquals("Premise-I", labelsPredicted.get(2).getTag());
        assertEquals("Premise-I", labelsPredicted.get(3).getTag());
        assertEquals("Premise-I", labelsPredicted.get(4).getTag());
        assertEquals("Premise-I", labelsPredicted.get(5).getTag());
        assertEquals("Premise-I", labelsPredicted.get(6).getTag());
        assertEquals("O", labelsPredicted.get(7).getTag());
        assertEquals("O", labelsPredicted.get(8).getTag());
    }

    @Test
    public void testSimplifiedTokenFromSentenceBIOOnlyB()
            throws Exception
    {
        SimplePipeline.runPipeline(jCas,
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentTokenBIOAnnotator.class),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedSentenceBIOAnnotator.class,
                        ArgumentSimplifiedSentenceBIOAnnotator.PARAM_START_EACH_SENTENCE_WITH_B,
                        true
                ),
                AnalysisEngineFactory.createEngineDescription(
                        ArgumentSimplifiedTokenBIOAnnotator.class,
                        ArgumentSimplifiedTokenBIOAnnotator.PARAM_RECREATE_FROM_SIMPLIFIED_SENTENCE_ANNOTATIONS,
                        true)
        );

        List<BIOSimplifiedTokenArgumentAnnotation> labelsPredicted = new ArrayList<BIOSimplifiedTokenArgumentAnnotation>(
                JCasUtil.select(jCas, BIOSimplifiedTokenArgumentAnnotation.class));

        // now each sentence starts with a B tag
        assertEquals("Premise-B", labelsPredicted.get(0).getTag());
        assertEquals("Premise-I", labelsPredicted.get(1).getTag());
        assertEquals("Premise-I", labelsPredicted.get(2).getTag());
        assertEquals("Premise-I", labelsPredicted.get(3).getTag());
        assertEquals("Premise-B", labelsPredicted.get(4).getTag());
        assertEquals("Premise-I", labelsPredicted.get(5).getTag());
        assertEquals("Premise-I", labelsPredicted.get(6).getTag());
        assertEquals("O", labelsPredicted.get(7).getTag());
        assertEquals("O", labelsPredicted.get(8).getTag());
    }
}