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

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

/**
 * @author Ivan Habernal
 */
public abstract class ArgumentBIOAnnotator
        extends JCasAnnotator_ImplBase
{
    /**
     * Coding granularity - BIO schema
     */
    public static final String BIO = "BIO";

    /**
     * Coding granularity - IO schema
     */
    public static final String IO = "IO";

    public static final String B_SUFFIX = "-B";

    public static final String I_SUFFIX = "-I";

    public static final String O_TAG = "O";

    /**
     * Granularity of labels
     * BIO or IO
     */
    public static final String PARAM_LABEL_GRANULARITY = "codingGranularity";
    @ConfigurationParameter(name = PARAM_LABEL_GRANULARITY, mandatory = true, defaultValue = BIO)
    protected String codingGranularity;

    @Override public void initialize(UimaContext context)
            throws ResourceInitializationException
    {
        super.initialize(context);

        if (!(BIO.equals(codingGranularity) || IO.equals(codingGranularity))) {
            throw new ResourceInitializationException(new IllegalArgumentException(
                    "Only BIO and IO labelGranularity is allowed, was " + codingGranularity));
        }
    }

}
