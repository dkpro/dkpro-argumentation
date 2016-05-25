/*
 * Copyright (c) the Department of Informatics, Technische Universität Darmstadt. All Rights Reserved.
 *
 * Unauthorized distribution of this file via any medium is strictly prohibited.
 */
package de.tudarmstadt.ukp.dkpro.argumentation.nio.writer.debug;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentRelation;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit;
import de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnitUtils;
import de.tudarmstadt.ukp.dkpro.argumentation.types.Claim;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

/**
 * @author <a href="mailto:shore@ukp.informatik.tu-darmstadt.de">Todd Shore</a>
 * @since Apr 29, 2016
 *
 */
final class DebugArgumentDumping {

	/**
	 * Dump argument components to String. Can be also used outside UIMA
	 * pipeline.
	 */
	public static void dump(final PrintWriter out, final JCas jCas, final boolean includeProperties,
			final boolean includeRelations) {

		final String documentId = DocumentMetaData.get(jCas).getDocumentId();

		out.println("======== #" + documentId + " begin ==========================");

		final Collection<ArgumentComponent> argumentComponents = JCasUtil.select(jCas, ArgumentComponent.class);

		out.println("----------------- ArgumentComponent -----------------");
		out.println("# of argument units: " + argumentComponents.size());

		for (final ArgumentComponent argumentComponent : argumentComponents) {
			out.println(argumentUnitToString(argumentComponent));

			if (includeProperties) {
				out.println(formatProperties(argumentComponent));
			}

			if (argumentComponent instanceof Claim) {
				final Claim claim = (Claim) argumentComponent;
				final String stance = claim.getStance();

				if (stance != null) {
					out.println("Stance: " + stance);
				}
			}
		}

		if (includeRelations) {
			final Collection<ArgumentRelation> argumentRelations = JCasUtil.select(jCas, ArgumentRelation.class);
			dumpRelations(out, argumentRelations, includeProperties);
		}

		out.println("======== #" + documentId + " end ==========================");
	}

	public static void dumpRelations(final PrintWriter out, final Collection<ArgumentRelation> argumentRelations,
			final boolean includeProperties) {
		out.println("----------------- ArgumentRelation -----------------");
		out.println("# of argument relations: " + argumentRelations.size());

		for (final ArgumentRelation argumentRelation : argumentRelations) {
			out.println(argumentRelation.getType().getShortName());
			out.println("   source: " + argumentUnitToString(argumentRelation.getSource()));
			out.println("   target: " + argumentUnitToString(argumentRelation.getTarget()));

			if (includeProperties) {
				out.println(formatProperties(argumentRelation));
			}
		}
	}

	/**
	 * Formats argument unit to string with type, position, and text content
	 *
	 * @param argumentUnit
	 *            argument unit
	 * @return string
	 */
	private static String argumentUnitToString(final ArgumentUnit argumentUnit) {
		return String.format("%s [%d, %d] \"%s\"", argumentUnit.getType().getShortName(), argumentUnit.getBegin(),
				argumentUnit.getEnd(), argumentUnit.getCoveredText());
	}

	/**
	 * Prints to string all non-null {@code properties} of the the given unit
	 *
	 * @param argumentUnit
	 *            argument unit
	 * @return string
	 */
	private static String formatProperties(final ArgumentUnit argumentUnit) {
		final StringBuilder sb = new StringBuilder("Properties:\n");

		final Properties properties = ArgumentUnitUtils.getProperties(argumentUnit);
		for (final Map.Entry<Object, Object> entry : properties.entrySet()) {
			if (entry.getValue() != null) {
				sb.append("   ");
				sb.append(entry.getKey());
				sb.append(": ");
				sb.append(entry.getValue());
				sb.append("\n");
			}
		}

		return sb.toString();
	}

	private DebugArgumentDumping() {
	}

}
