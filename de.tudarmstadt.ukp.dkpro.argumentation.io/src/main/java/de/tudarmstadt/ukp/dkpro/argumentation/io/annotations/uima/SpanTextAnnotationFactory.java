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
package de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.uima;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.Attribute;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.ImmutableSpan;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.ImmutableSpanText;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.MutableSpanTextLabel;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.SpanTextLabel;
import de.tudarmstadt.ukp.dkpro.argumentation.types.Claim;

/**
 * @author Todd Shore
 * @since May 2, 2016
 *
 */
public final class SpanTextAnnotationFactory
    implements Function<Annotation, SpanTextLabel>
{

    /**
     * {@link SingletonHolder} is loaded on the first execution of
     * {@link SpanTextAnnotationFactory#getInstance()} or the first access to
     * {@link SingletonHolder#INSTANCE}, not before.
     *
     * @author <a href="http://www.cs.umd.edu/~pugh/">Bill Pugh</a>
     * @see <a href= "https://en.wikipedia.org/wiki/Singleton_pattern#The_solution_of_Bill_Pugh">
     *      https://en.wikipedia.org/wiki/Singleton_pattern# The_solution_of_Bill_Pugh</a>
     */
    private static final class SingletonHolder
    {
        /**
         * A singleton instance of {@link SpanTextAnnotationFactory}.
         */
        private static final SpanTextAnnotationFactory INSTANCE = new SpanTextAnnotationFactory();
    }

    public static SpanTextAnnotationFactory getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static Map<Attribute, Object> createAttrMap(final Annotation annotation)
    {
        final Map<Attribute, Object> result = new EnumMap<>(Attribute.class);
        if (annotation instanceof Claim) {
            result.put(Attribute.CATEGORY, ((Claim) annotation).getStance());
        }
        return result;
    }

    private SpanTextAnnotationFactory()
    {
    }

    @Override
    public SpanTextLabel apply(final Annotation annotation)
    {
        final ImmutableSpan span = new ImmutableSpan(annotation.getBegin(), annotation.getEnd());
        final ImmutableSpanText spanText = new ImmutableSpanText(span, annotation.getCoveredText());
        final Map<Attribute, Object> attrs = createAttrMap(annotation);
        return new MutableSpanTextLabel(spanText, annotation.getType().getShortName(), attrs);
    }

}
