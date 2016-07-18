/*
 * Copyright 2016 Ubiquitous Knowledge Processing (UKP) Lab, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.io.writer.json;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentRelation;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Jul 14, 2016
 *
 */
final class ArgumentTransitionTableCollector
    implements Collector<ArgumentRelation, int[], int[]>
{

    private static final Set<java.util.stream.Collector.Characteristics> CHARACTERISTICS = EnumSet
            .of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED,
                    Characteristics.CONCURRENT);

    private static final Log LOG = LogFactory.getLog(ArgumentTransitionTableCollector.class);

    private static String createStrRepr(final Annotation source)
    {
        return String.format("[%d, %d, %s]", source.getBegin(), source.getEnd(),
                source.getType().getShortName());
    }

    private final BiConsumer<int[], ArgumentRelation> accumulator;
    private final BinaryOperator<int[]> combiner;

    private final Supplier<int[]> supplier;

    /**
     *
     */
    public ArgumentTransitionTableCollector(final Supplier<int[]> resultContainerSupplier,
            final int initialArrayElementValue,
            final ToIntFunction<? super Annotation> annotationIdGetter)
    {
        supplier = resultContainerSupplier;
        accumulator = (result, argumentRelation) -> {
            final ArgumentUnit source = argumentRelation.getSource();
            final int sourceSpanAnnotationId = annotationIdGetter.applyAsInt(source);
            if (sourceSpanAnnotationId < 0) {
                LOG.error(String.format("Source span %s not found in annotation matrix.",
                        createStrRepr(source)));
            }
            else {
                final ArgumentUnit target = argumentRelation.getTarget();
                final int targetSpanAnnotationId = annotationIdGetter.applyAsInt(target);
                if (targetSpanAnnotationId < 0) {
                    LOG.error(String.format("Target span %s not found in annotation matrix.",
                            createStrRepr(target)));
                }
                else {
                    // The target serves as a key instead of the source because
                    // it is possible that e.g. one claim has multiple premises
                    result[sourceSpanAnnotationId] = targetSpanAnnotationId;
                }

            }
        };

        combiner = (result, partialResultToAdd) -> {

            for (int i = 0; i < partialResultToAdd.length; ++i) {
                final int elemToAdd = partialResultToAdd[i];
                // Avoid re-setting values in the result array to the initial value
                if (elemToAdd != initialArrayElementValue) {
                    result[i] = elemToAdd;
                }

            }
            return result;
        };
    }

    @Override
    public BiConsumer<int[], ArgumentRelation> accumulator()
    {
        return accumulator;
    }

    @Override
    public Set<java.util.stream.Collector.Characteristics> characteristics()
    {
        return CHARACTERISTICS;
    }

    @Override
    public BinaryOperator<int[]> combiner()
    {
        return combiner;
    }

    @Override
    public Function<int[], int[]> finisher()
    {
        return Function.identity();
    }

    @Override
    public Supplier<int[]> supplier()
    {
        return supplier;
    }

}
