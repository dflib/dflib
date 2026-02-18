package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;

/**
 * Parser rule is a small function that reads {@code char[]} data and drives that whole parsing process.
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 * @since 2.0.0
 */
@FunctionalInterface
public interface ParserRule {

    // Signal that the rule can't consume yet the data it was provided.
    int CONTINUE = -1;

    int consume(RuleContext context, DataSlice slice);

    /**
     * Allows the rule to validate it's state at the end of file.
     * @param context parsing context to use
     */
    default void validateAtEof(RuleContext context) {
    }
}
