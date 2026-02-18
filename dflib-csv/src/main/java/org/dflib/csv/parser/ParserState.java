package org.dflib.csv.parser;

/**
 * Parsing states. The current state determines parser rule flow in the {@link ParserRuleFlow}.
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @see org.dflib.csv.parser.rules.ParserRule
 * @see ParserRuleFlow
 *
 * @since 2.0.0
 */
public enum ParserState {
    NO,
    EMPTY_ROW,
    COMMENT,
    SKIP_ROW,
    START_ANY,
    START_QUOTED_OPTIONAL,
    START_QUOTED,
    START_UNQUOTED,
    END_QUOTED,
    DELIMITER,
    END_OF_LINE,
    DELIMITER_OR_END_OF_LINE
}
