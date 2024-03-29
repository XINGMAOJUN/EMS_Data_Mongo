package com.persagy.mongo.core.annotation;

import com.mongodb.client.model.CollationAlternate;
import com.mongodb.client.model.CollationCaseFirst;
import com.mongodb.client.model.CollationMaxVariable;
import com.mongodb.client.model.CollationStrength;

/**
 * Defines the collation options for an index
 * 
 * @since 1.3
 */
public @interface Collation {
	/**
	 * Causes secondary differences to be considered in reverse order, as it is done
	 * in the French language
	 *
	 * @return the backwards value
	 */
	boolean backwards() default false;

	/**
	 * Turns on case sensitivity
	 *
	 * @return the case level value
	 */
	boolean caseLevel() default false;

	/**
	 * @return the locale
	 *
	 * @see <a href="http://userguide.icu-project.org/locale">ICU User Guide -
	 *      Locale</a>
	 */
	String locale() default "a";

	/**
	 * @return the normalization value. If true, normalizes text into Unicode NFD.
	 */
	boolean normalization() default false;

	/**
	 * @return the numeric ordering. if true will order numbers based on numerical
	 *         order and not collation order
	 */
	boolean numericOrdering() default false;

	/**
	 * Controls whether spaces and punctuation are considered base characters
	 *
	 * @return the alternate
	 */
	CollationAlternate alternate() default CollationAlternate.NON_IGNORABLE;

	/**
	 * Determines if Uppercase or lowercase values should come first
	 *
	 * @return the collation case first value
	 */
	CollationCaseFirst caseFirst() default CollationCaseFirst.OFF;

	/**
	 * @return the maxVariable
	 */
	CollationMaxVariable maxVariable() default CollationMaxVariable.PUNCT;

	/**
	 * @return the collation strength
	 */
	CollationStrength strength() default CollationStrength.TERTIARY;
}
