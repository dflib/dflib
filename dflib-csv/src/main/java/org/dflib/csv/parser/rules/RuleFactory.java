package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.format.CsvFormat;

/**
 * Simple interface for the {@link ParserRule} creation.
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @see RuleBuilder
 * @since 2.0.0
 */
@FunctionalInterface
public interface RuleFactory {
    ParserRule create(CsvFormat format);
}
