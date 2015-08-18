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

import de.tudarmstadt.ukp.dkpro.argumentation.misc.utils.ArgumentUtils;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent;
import de.tudarmstadt.ukp.dkpro.argumentation.types.BIOSimplifiedSentenceArgumentAnnotation;
import de.tudarmstadt.ukp.dkpro.argumentation.types.BIOSimplifiedTokenArgumentAnnotation;
import de.tudarmstadt.ukp.dkpro.argumentation.types.BIOTokenArgumentAnnotation;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.Collection;
import java.util.List;

/**
 * For each sentence that contains an argument component, it creates new annotations for each
 * token (type {@link de.tudarmstadt.ukp.dkpro.argumentation.types.BIOSimplifiedTokenArgumentAnnotation})
 * so the argument component covers the entire sentence. If two or more argument components
 * are present in the sentence, the longest one is chosen. If no component is found,
 * a "O" label is used as expected.
 *
 * @author Ivan Habernal
 */
@TypeCapability(inputs = {
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent"
}, outputs = {
        "de.tudarmstadt.ukp.dkpro.argumentation.types.BIOSimplifiedTokenArgumentAnnotation"
})
public class ArgumentSimplifiedTokenBIOAnnotator
        extends ArgumentBIOAnnotator
{
    // for debugging purposes
    int outsideSentencesAnnotations = 0;

    public static final String PARAM_RECREATE_FROM_SIMPLIFIED_SENTENCE_ANNOTATIONS = "recreateFromSimplifiedSentenceAnnotations";
    /**
     * If true, the {@code BIOSimplifiedTokenArgumentAnnotation} annotations will not be created
     * based on existing {@code BIOTokenArgumentAnnotation} but on simplified annotations
     * from {@code BIOSimplifiedTokenArgumentAnnotation}. See tests for examples.
     */
    @ConfigurationParameter(name = PARAM_RECREATE_FROM_SIMPLIFIED_SENTENCE_ANNOTATIONS,
            mandatory = true, defaultValue = "false")
    protected boolean recreateFromSimplifiedSentenceAnnotations;

    @Override public void initialize(UimaContext context)
            throws ResourceInitializationException
    {
        super.initialize(context);
    }

    /**
     * Annotations are created based on {@code BIOTokenArgumentAnnotation}
     *
     * @param aJCas jcas
     * @throws AnalysisEngineProcessException
     */
    protected void processFromBIOTokenArgumentAnnotation(JCas aJCas)
            throws AnalysisEngineProcessException
    {
        for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {

            // all argument components present in the sentence
            List<ArgumentComponent> argumentComponents = ArgumentUtils
                    .selectOverlappingComponentsWithoutPathosAndImplicit(sentence, aJCas);

            // empty labels = "O"
            if (argumentComponents.isEmpty()) {
                for (Token token : JCasUtil.selectCovered(aJCas, Token.class, sentence)) {
                    BIOSimplifiedTokenArgumentAnnotation sequenceLabel = new BIOSimplifiedTokenArgumentAnnotation(
                            aJCas);
                    sequenceLabel.setBegin(token.getBegin());
                    sequenceLabel.setEnd(token.getEnd());
                    sequenceLabel.setTag(O_TAG);
                    sequenceLabel.addToIndexes();
                }
            }

            else {
                // find the maximum spanning argument component
                ArgumentComponent coveringArgumentComponent = selectMainArgumentComponent(
                        argumentComponents);

                List<Token> tokens = JCasUtil.selectCovered(aJCas, Token.class, sentence);

                for (int i = 0; i < tokens.size(); i++) {
                    Token token = tokens.get(i);

                    StringBuilder outputLabel = new StringBuilder(
                            coveringArgumentComponent.getClass().getSimpleName());

                    // component annotation starts in the sentence, we add "B" to the first token
                    if ((i == 0) && (coveringArgumentComponent.getBegin() >= sentence.getBegin()) &&
                            BIO.equals(this.codingGranularity)) {
                        outputLabel.append(B_SUFFIX);
                    }
                    else {
                        outputLabel.append(I_SUFFIX);
                    }

                    BIOSimplifiedTokenArgumentAnnotation label = new BIOSimplifiedTokenArgumentAnnotation(
                            aJCas);
                    label.setBegin(token.getBegin());
                    label.setEnd(token.getEnd());
                    label.setTag(outputLabel.toString());
                    label.addToIndexes();
                }
            }
        }
    }

    /**
     * Annotations are re-created from existing {@code BIOSimplifiedSentenceArgumentAnnotation}
     *
     * @param aJCas jcas
     * @throws AnalysisEngineProcessException
     */
    protected void processFromBIOSimplifiedSentenceArgumentAnnotation(JCas aJCas)
            throws AnalysisEngineProcessException
    {
        Collection<BIOSimplifiedSentenceArgumentAnnotation> sentenceArgumentAnnotations = JCasUtil
                .select(aJCas, BIOSimplifiedSentenceArgumentAnnotation.class);
        Collection<Sentence> sentences = JCasUtil.select(aJCas, Sentence.class);

        // BIOSimplifiedSentenceArgumentAnnotation must match with sentences
        if (sentences.size() != sentenceArgumentAnnotations.size()) {
            throw new AnalysisEngineProcessException(new IllegalArgumentException(sentences.size()
                    + " BIOSimplifiedSentenceArgumentAnnotation annotations expected, but "
                    + sentenceArgumentAnnotations.size() + " found"));
        }

        // iterate over sentence-level annotations
        for (BIOSimplifiedSentenceArgumentAnnotation sentenceArgumentAnnotation : sentenceArgumentAnnotations) {
            List<Token> tokens = JCasUtil.selectCovered(aJCas, Token.class,
                    sentenceArgumentAnnotation);

            // get the tag
            String sentenceTag = sentenceArgumentAnnotation.getTag();
            String sentenceTagWithoutSuffix = sentenceArgumentAnnotation.getTag().split("-")[0];

            boolean firstTokenIsBegin = sentenceTag.endsWith(B_SUFFIX);

            // iterate over tokens and create appropriate BIOSimplifiedTokenArgumentAnnotation
            for (int i = 0; i < tokens.size(); i++) {
                Token token = tokens.get(i);

                BIOSimplifiedTokenArgumentAnnotation label = new BIOSimplifiedTokenArgumentAnnotation(
                        aJCas);
                label.setBegin(token.getBegin());
                label.setEnd(token.getEnd());
                label.addToIndexes();

                // the output label
                if (O_TAG.equals(sentenceTag)) {
                    label.setTag(O_TAG);
                }
                else if (BIO.equals(this.codingGranularity) && (i == 0) && firstTokenIsBegin) {
                    label.setTag(sentenceTagWithoutSuffix + B_SUFFIX);
                }
                else {
                    label.setTag(sentenceTagWithoutSuffix + I_SUFFIX);
                }
            }
        }

    }

    @Override
    public void process(JCas aJCas)
            throws AnalysisEngineProcessException
    {
        if (this.recreateFromSimplifiedSentenceAnnotations) {
            processFromBIOSimplifiedSentenceArgumentAnnotation(aJCas);
        }
        else {
            processFromBIOTokenArgumentAnnotation(aJCas);
        }

        // are there any tokens outside of sentences?
        for (Token token : JCasUtil.select(aJCas, Token.class)) {
            List<BIOSimplifiedTokenArgumentAnnotation> covered = JCasUtil
                    .selectCovering(aJCas, BIOSimplifiedTokenArgumentAnnotation.class, token);
            if (covered.isEmpty()) {
                BIOSimplifiedTokenArgumentAnnotation label = new BIOSimplifiedTokenArgumentAnnotation(
                        aJCas);
                label.setBegin(token.getBegin());
                label.setEnd(token.getEnd());
                label.setTag(O_TAG);
                label.addToIndexes();
                outsideSentencesAnnotations++;
            }
        }

        Collection<BIOTokenArgumentAnnotation> goldLabels = JCasUtil
                .select(aJCas, BIOTokenArgumentAnnotation.class);
        Collection<Token> goldTokens = JCasUtil.select(aJCas, Token.class);

        if (goldLabels.size() != goldTokens.size()) {
            throw new AnalysisEngineProcessException(
                    new IllegalStateException("GoldOutcomes and TokenSize are different sizes. " +
                            "Gold: " + goldLabels.size() + ", Token: " + goldTokens.size()));
        }
    }

    /**
     * Selects the main argument component from a list of components that are present in the
     * sentence; currently the longest
     *
     * @param argumentComponents list of argument components
     * @return argument component
     */
    protected ArgumentComponent selectMainArgumentComponent(
            List<ArgumentComponent> argumentComponents)
    {
        ArgumentComponent result = null;

        int maxLength = Integer.MIN_VALUE;
        for (ArgumentComponent argumentComponent : argumentComponents) {
            int length = argumentComponent.getEnd() - argumentComponent.getBegin();

            if (length > maxLength) {
                maxLength = length;
                result = argumentComponent;
            }
        }

        if (result == null) {
            throw new IllegalStateException("Couldn't find maximum arg. component");
        }

        return result;
    }

    @Override
    public void collectionProcessComplete()
            throws AnalysisEngineProcessException
    {
        super.collectionProcessComplete();

        if (outsideSentencesAnnotations > 0) {
            getLogger().warn("Tokens outside annotated sentences: " + outsideSentencesAnnotations);
        }
    }
}
