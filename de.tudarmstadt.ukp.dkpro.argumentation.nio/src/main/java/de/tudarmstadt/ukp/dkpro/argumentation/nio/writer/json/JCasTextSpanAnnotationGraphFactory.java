package de.tudarmstadt.ukp.dkpro.argumentation.nio.writer.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.argumentation.annotations.SpanAnnotationGraph;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.TextSpanAnnotation;
import de.tudarmstadt.ukp.dkpro.argumentation.annotations.uima.TextSpanAnnotationFactory;
import de.tudarmstadt.ukp.dkpro.argumentation.fastutil.ints.ReverseLookupOrderedSet;
import de.tudarmstadt.ukp.dkpro.argumentation.nio.writer.InconsistentSpanAnnotationException;
import de.tudarmstadt.ukp.dkpro.argumentation.nio.writer.SpanAnnotationException;
import de.tudarmstadt.ukp.dkpro.argumentation.nio.writer.SpanAnnotationNotFoundException;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentRelation;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit;
import de.tudarmstadt.ukp.math.Sparse3DObjectMatrix;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;

@SuppressWarnings("deprecation")
final class JCasTextSpanAnnotationGraphFactory implements Function<JCas, SpanAnnotationGraph<TextSpanAnnotation>> {

	private static final int ESTIMATED_ANNOTATION_MAP_MAX_CAPACITY = 2;

	private static final int ESTIMATED_SPAN_BEGIN_TO_END_MAP_MAX_CAPACITY = 3;

	private static final Log LOG = LogFactory.getLog(JCasTextSpanAnnotationGraphFactory.class);

	private static int[] createArgumentTransitionTable(final Collection<ArgumentRelation> argumentRelations,
			final Sparse3DObjectMatrix<String, TextSpanAnnotation> spanAnnotationMatrix,
			final Object2IntMap<TextSpanAnnotation> spanAnnotationIds) {
		final int[] result = new int[spanAnnotationIds.size()];
		for (final ArgumentRelation argumentRelation : argumentRelations) {
			final ArgumentUnit source = argumentRelation.getSource();
			try {
				final int sourceSpanAnnotationId = getAnnotationId(source, spanAnnotationMatrix, spanAnnotationIds);
				final ArgumentUnit target = argumentRelation.getTarget();
				final int targetSpanAnnotatonId = getAnnotationId(target, spanAnnotationMatrix, spanAnnotationIds);
				// The target serves as a key instead of the source because
				// it is possible that e.g. one claim has multiple premises
				result[sourceSpanAnnotationId] = targetSpanAnnotatonId;

			} catch (final SpanAnnotationException e) {
				// TODO: implement error handling
				LOG.error(e);
			}
		}

		return result;
	}

	private static int getAnnotationId(final Annotation source,
			final Sparse3DObjectMatrix<String, TextSpanAnnotation> spanAnnotationMatrix,
			final Object2IntMap<TextSpanAnnotation> spanAnnotationIds)
			throws InconsistentSpanAnnotationException, SpanAnnotationNotFoundException {
		final int result;

		final int begin = source.getBegin();
		final int end = source.getEnd();
		final String annotationType = source.getType().getShortName();
		final TextSpanAnnotation spanAnnotation = spanAnnotationMatrix.get3DValue(begin, end, annotationType);
		if (spanAnnotation == null) {
			throw new SpanAnnotationNotFoundException(begin, end, annotationType);
		} else {
			result = spanAnnotationIds.getInt(spanAnnotation);
		}

		return result;

	}

	@Override
	public SpanAnnotationGraph<TextSpanAnnotation> apply(final JCas jCas) {
		final ReverseLookupOrderedSet<TextSpanAnnotation> spanAnnotationVector;
		final Sparse3DObjectMatrix<String, TextSpanAnnotation> spanAnnotationMatrix;
		{
			final Collection<ArgumentComponent> argumentComponents = JCasUtil.select(jCas, ArgumentComponent.class);

			{
				final int argumentComponentCount = argumentComponents.size();
				LOG.info(String.format("Processing %d argument components.", argumentComponentCount));
				// The list of all annotations, their index in the list serving
				// as
				// their
				// ID
				spanAnnotationVector = new ReverseLookupOrderedSet<TextSpanAnnotation>(
						new ArrayList<TextSpanAnnotation>(argumentComponentCount));
				// Just use the size "argumentComponentCount" directly here
				// because
				// it
				// is assumed that spans
				// don't overlap
				spanAnnotationMatrix = new Sparse3DObjectMatrix<>(
						new Int2ObjectOpenHashMap<>(argumentComponentCount + 1),
						ESTIMATED_SPAN_BEGIN_TO_END_MAP_MAX_CAPACITY, ESTIMATED_ANNOTATION_MAP_MAX_CAPACITY);
			}

			for (final ArgumentComponent argumentComponent : argumentComponents) {
				final TextSpanAnnotation spanAnnotation = TextSpanAnnotationFactory.getInstance()
						.apply(argumentComponent);
				final int begin = spanAnnotation.getBegin();
				final int end = spanAnnotation.getEnd();
				final String annotationType = spanAnnotation.getAnnotationType();
				// final SpanAnnotationEdge edge = new
				// SpanAnnotationEdge(annotationType);
				final Map<String, TextSpanAnnotation> spanAnnotations = spanAnnotationMatrix.fetch3DMap(begin, end);
				final TextSpanAnnotation oldSpanAnnotation = spanAnnotations.put(annotationType, spanAnnotation);
				if (oldSpanAnnotation != null) {
					LOG.warn(String.format("Annotation type \"%s\" already exists for span [%d, %d]; Overwriting.",
							annotationType, begin, end));
				}
				spanAnnotationVector.add(spanAnnotation);
			}
		}

		final Collection<ArgumentRelation> argumentRelations = JCasUtil.select(jCas, ArgumentRelation.class);
		final Object2IntMap<TextSpanAnnotation> spanAnnotationIds = spanAnnotationVector.getReverseLookupMap();
		LOG.info(String.format("Processing %d argument relations.", argumentRelations.size()));
		final int[] argumentTransitionTable = createArgumentTransitionTable(argumentRelations, spanAnnotationMatrix,
				spanAnnotationIds);

		return new SpanAnnotationGraph<TextSpanAnnotation>(spanAnnotationVector, argumentTransitionTable);
	}

}
