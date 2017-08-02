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

package org.dkpro.argumentation.preprocessing.annotation;

import org.dkpro.argumentation.misc.utils.ArgumentUtils;
import org.dkpro.argumentation.types.ArgumentComponent;
import org.dkpro.argumentation.types.BIOTokenArgumentAnnotation;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.util.List;

/**
 * Annotates the tokens as a sequence of BIO labels, such as
 * B-CLAIM; I-CLAIM; O; O; B-PREMISE; I-PREMISE; B-PREMISE; O; ...
 * with type {@link org.dkpro.argumentation.types.BIOTokenArgumentAnnotation}
 * <p>
 * If a token contains multiple annotations (i.e. was annotated as a claim AND backing),
 * an exception is thrown.
 * </p>
 *
 * @author Ivan Habernal
 */
@TypeCapability(inputs = {
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
        "org.dkpro.argumentation.types.ArgumentComponent"
}, outputs = {
        "org.dkpro.argumentation.types.BIOTokenArgumentAnnotation"
})
public class ArgumentTokenBIOAnnotator
        extends ArgumentBIOAnnotator
{

    /**
     * If true, annotator only warns on multiple annotations per token; by default it throws
     * and exception
     */
    public static final String PARAM_LENIENT = "lenient";
    @ConfigurationParameter(name = PARAM_LENIENT, mandatory = true, defaultValue = "false")
    private boolean lenient;

    /**
     * Returns a label for the annotated token
     *
     * @param argumentComponent covering argument component
     * @param token             token
     * @return BIO label
     */
    protected String getLabel(ArgumentComponent argumentComponent, Token token)
    {
        StringBuilder sb = new StringBuilder(argumentComponent.getClass().getSimpleName());

        if ("BIO".equals(this.codingGranularity)) {
            // Does the component begin here?
            if (argumentComponent.getBegin() == token.getBegin()) {
                sb.append(B_SUFFIX);
            }
            else {
                sb.append(I_SUFFIX);
            }
        }
        else {
            sb.append(I_SUFFIX);
        }

        return sb.toString();
    }

    @Override
    public void process(JCas jCas)
            throws AnalysisEngineProcessException
    {
        for (Token token : JCasUtil.select(jCas, Token.class)) {
            List<ArgumentComponent> covering = ArgumentUtils.removeAppealToEmotion(JCasUtil
                    .selectCovering(ArgumentComponent.class, token));

            BIOTokenArgumentAnnotation sequenceLabel = new BIOTokenArgumentAnnotation(jCas);

            if (covering.isEmpty()) {
                sequenceLabel.setTag(O_TAG);
            }
            else if (covering.size() == 1) {
                ArgumentComponent argumentComponent = covering.iterator().next();

                String label = getLabel(argumentComponent, token);

                sequenceLabel.setTag(label);
            }
            else {
                String message = "More than one annotation found for particular word!" + token
                        .getCoveredText() + ", " + covering;
                if (this.lenient) {
                    getLogger().warn(message);
                }
                else {
                    throw new AnalysisEngineProcessException(new IllegalArgumentException(message));
                }
            }

            sequenceLabel.setBegin(token.getBegin());
            sequenceLabel.setEnd(token.getEnd());
            sequenceLabel.addToIndexes();
        }
    }
}
