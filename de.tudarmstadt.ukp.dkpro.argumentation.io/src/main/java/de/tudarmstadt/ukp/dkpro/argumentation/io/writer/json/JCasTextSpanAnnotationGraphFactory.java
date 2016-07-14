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
package de.tudarmstadt.ukp.dkpro.argumentation.io.writer.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.argumentation.fastutil.ints.ReverseLookupOrderedSet;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.Attribute;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.Span;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.SpanAnnotationGraph;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.SpanTextLabel;
import de.tudarmstadt.ukp.dkpro.argumentation.io.annotations.uima.SpanTextAnnotationFactory;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentRelation;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit;
import de.tudarmstadt.ukp.math.Sparse3DObjectMatrix;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

@SuppressWarnings("deprecation")
final class JCasTextSpanAnnotationGraphFactory
    implements Function<JCas, SpanAnnotationGraph<SpanTextLabel>>
{

    private static final int ESTIMATED_ANNOTATION_MAP_MAX_CAPACITY = 2;

    private static final int ESTIMATED_SPAN_BEGIN_TO_END_MAP_MAX_CAPACITY = 3;

    private static final Log LOG = LogFactory.getLog(JCasTextSpanAnnotationGraphFactory.class);

    private static SpanTextLabel getSpanTextLabel(final Annotation annot,
            final Sparse3DObjectMatrix<String, SpanTextLabel> spanAnnotationMatrix)
    {
        final Map<String, SpanTextLabel> labelAnnots = getSpanTextLabels(annot,
                spanAnnotationMatrix);
        return labelAnnots.get(annot.getType().getShortName());
    }

    private static Map<String, SpanTextLabel> getSpanTextLabels(final Annotation annot,
            final Sparse3DObjectMatrix<String, SpanTextLabel> spanAnnotationMatrix)
    {
        final int begin = annot.getBegin();
        final int end = annot.getEnd();
        return spanAnnotationMatrix.get3DMap(begin, end);
    }

    private static int getAnnotationId(final Annotation source,
            final Sparse3DObjectMatrix<String, SpanTextLabel> spanAnnotationMatrix,
            final Object2IntMap<SpanTextLabel> spanAnnotationIds)
    {
        final SpanTextLabel spanAnnotation = getSpanTextLabel(source, spanAnnotationMatrix);
        return spanAnnotation == null ? -1 : spanAnnotationIds.getInt(spanAnnotation);
    }

    @Override
    public SpanAnnotationGraph<SpanTextLabel> apply(final JCas jCas)
    {
        final ReverseLookupOrderedSet<SpanTextLabel> spanAnnotationVector;
        final Sparse3DObjectMatrix<String, SpanTextLabel> spanAnnotationMatrix;
        {
            final Collection<ArgumentComponent> argumentComponents = JCasUtil.select(jCas,
                    ArgumentComponent.class);

            {
                final int argumentComponentCount = argumentComponents.size();
                LOG.info(String.format("Processing %d argument components.",
                        argumentComponentCount));
                // The list of all annotations, their index in the list serving
                // as
                // their
                // ID
                spanAnnotationVector = new ReverseLookupOrderedSet<>(
                        new ArrayList<SpanTextLabel>(argumentComponentCount));
                // Just use the size "argumentComponentCount" directly here
                // because
                // it
                // is assumed that spans
                // don't overlap
                spanAnnotationMatrix = new Sparse3DObjectMatrix<>(
                        new Int2ObjectOpenHashMap<>(argumentComponentCount + 1),
                        ESTIMATED_SPAN_BEGIN_TO_END_MAP_MAX_CAPACITY,
                        ESTIMATED_ANNOTATION_MAP_MAX_CAPACITY);
            }

            for (final ArgumentComponent argumentComponent : argumentComponents) {
                final SpanTextLabel spanAnnotation = SpanTextAnnotationFactory.getInstance()
                        .apply(argumentComponent);
                final Span span = spanAnnotation.getSpanText().getSpan();
                final int begin = span.getBegin();
                final int end = span.getEnd();
                final String label = spanAnnotation.getLabel();
                final Map<String, SpanTextLabel> spanAnnotations = spanAnnotationMatrix
                        .fetch3DMap(begin, end);
                final SpanTextLabel oldSpanAnnotation = spanAnnotations.put(label, spanAnnotation);
                if (oldSpanAnnotation != null) {
                    LOG.warn(String.format(
                            "Annotation label \"%s\" already exists for span [%d, %d]; Overwriting.",
                            label, begin, end));
                }
                spanAnnotationVector.add(spanAnnotation);
            }
        }

        final Collection<ArgumentRelation> argumentRelations = JCasUtil.select(jCas,
                ArgumentRelation.class);
        final Object2IntMap<SpanTextLabel> spanAnnotationIds = spanAnnotationVector
                .getReverseLookupMap();
        LOG.info(String.format("Processing %d argument relations.", argumentRelations.size()));

        final int initialTableRelationValue = -1;
        final Supplier<int[]> transitionTableArraySupplier = () -> {
            final int[] result = new int[spanAnnotationIds.size()];
            // Pre-fill the array in the case that there is no transition for a
            // given annotation
            Arrays.fill(result, initialTableRelationValue);
            return result;
        };
        final ArgumentTransitionTableCollector argTransitionTableCollector = new ArgumentTransitionTableCollector(
                transitionTableArraySupplier, initialTableRelationValue,
                annot -> getAnnotationId(annot, spanAnnotationMatrix, spanAnnotationIds));

        final Stream<ArgumentRelation> argumentRelationStream = argumentRelations.stream()
                .map(argumentRelation -> {
                    final ArgumentUnit source = argumentRelation.getSource();
                    final Map<Attribute, Object> sourceAnnotAttrs = getSpanTextLabel(source,
                            spanAnnotationMatrix).getAttributes();
                    // If the source annotation doesn't have its own category already set, set it to
                    // the type label of the relation between it and its target
                    sourceAnnotAttrs.putIfAbsent(Attribute.CATEGORY,
                            argumentRelation.getType().getShortName());
                    return argumentRelation;
                });
        final int[] argumentTransitionTable = argumentRelationStream
                .collect(argTransitionTableCollector);

        return new SpanAnnotationGraph<>(spanAnnotationVector, argumentTransitionTable);
    }

}
