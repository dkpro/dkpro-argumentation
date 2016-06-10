package de.tudarmstadt.ukp.dkpro.argumentation.annotations;

public interface Span extends Comparable<Span> {

	@Override
	default int compareTo(final Span o) {
		int result = Integer.compare(getBegin(), o.getBegin());
		if (result == 0) {
			result = Integer.compare(getEnd(), o.getEnd());
		}
		return result;
	}

	/**
	 * @return the begin
	 */
	int getBegin();

	/**
	 * @return the end
	 */
	int getEnd();

}