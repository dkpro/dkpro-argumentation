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
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.util.List;

/**
 * @author Ivan Habernal
 */
@TypeCapability(inputs = {
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent"
}, outputs = {
        "de.tudarmstadt.ukp.dkpro.argumentation.types.BIOSimplifiedSentenceArgumentAnnotation"
})
public class ArgumentSimplifiedSentenceBIOAnnotator
        extends ArgumentSimplifiedTokenBIOAnnotator
{

    /**
     * If set to true, each sentence is considered as a begin of the argument component, so
     * it is always labeled with XXX-B. This allows pretending sentence independence
     * (multi-sentence components)
     */
    public static final String PARAM_START_EACH_SENTENCE_WITH_B = "startEachSentenceWithB";
    @ConfigurationParameter(name = PARAM_START_EACH_SENTENCE_WITH_B, mandatory = true,
            defaultValue = "false")
    protected boolean startEachSentenceWithB;

    @Override
    public void initialize(UimaContext context)
            throws ResourceInitializationException
    {

        super.initialize(context);
    }

    @Override
    public void process(JCas aJCas)
            throws AnalysisEngineProcessException
    {
        for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {

            // all argument components present in the sentence
            List<ArgumentComponent> argumentComponents = ArgumentUtils
                    .selectOverlappingComponentsWithoutPathosAndImplicit(sentence, aJCas);

            // create new sentence-long annotation
            BIOSimplifiedSentenceArgumentAnnotation sentenceArgumentAnnotation =
                    new BIOSimplifiedSentenceArgumentAnnotation(aJCas, sentence.getBegin(),
                            sentence.getEnd());
            sentenceArgumentAnnotation.addToIndexes();

            if (argumentComponents.isEmpty()) {
                // empty labels = "O"
                sentenceArgumentAnnotation.setTag(O_TAG);
            }

            else {
                // find the maximum spanning argument component
                ArgumentComponent coveringArgumentComponent = selectMainArgumentComponent(
                        argumentComponents);

                StringBuilder outputLabel = new StringBuilder(
                        coveringArgumentComponent.getClass().getSimpleName());

                // does the annotation start in this sentence?
                if (this.startEachSentenceWithB || (
                        coveringArgumentComponent.getBegin() >= sentence.getBegin() &&
                                BIO.equals(this.codingGranularity))) {
                    outputLabel.append(B_SUFFIX);
                }
                else {
                    // otherwise it continues from the previous one
                    outputLabel.append(I_SUFFIX);
                }

                // set the label
                sentenceArgumentAnnotation.setTag(outputLabel.toString());
            }
        }
    }
}
