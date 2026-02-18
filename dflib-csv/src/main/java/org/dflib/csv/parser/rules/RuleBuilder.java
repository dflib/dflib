package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.format.CsvFormat;

import java.util.EnumMap;

/**
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @since 2.0.0
 */
public class RuleBuilder {

    static final EnumMap<ParserState, RuleFactory> RULE_FACTORIES = new EnumMap<>(ParserState.class);

    static {
        RULE_FACTORIES.put(ParserState.NO, new NoopFactory());
        RULE_FACTORIES.put(ParserState.EMPTY_ROW, new EmptyRowFactory());
        RULE_FACTORIES.put(ParserState.COMMENT, new CommentStartFactory());
        RULE_FACTORIES.put(ParserState.SKIP_ROW, new OffsetSkipFactory());
        RULE_FACTORIES.put(ParserState.START_ANY, new ColumnStartAnyFactory());
        RULE_FACTORIES.put(ParserState.START_QUOTED_OPTIONAL, new ColumnStartQuotedOptionalFactory());
        RULE_FACTORIES.put(ParserState.START_QUOTED, new ColumnStartQuotedFactory());
        RULE_FACTORIES.put(ParserState.START_UNQUOTED, new ColumnStartUnquotedFactory());
        RULE_FACTORIES.put(ParserState.END_QUOTED, new ColumnEndQuotedFactory());
        RULE_FACTORIES.put(ParserState.DELIMITER, new DelimiterFactory());
        RULE_FACTORIES.put(ParserState.END_OF_LINE, new EndOfLineFactory());
        RULE_FACTORIES.put(ParserState.DELIMITER_OR_END_OF_LINE, new DelimiterOrEndOfLineFactory());
    }

    public static ParserRule[] buildRules(CsvFormat format) {
        ParserState[] states = ParserState.values();
        ParserRule[] rules = new ParserRule[states.length];
        for (ParserState state : states) {
            rules[state.ordinal()] = RULE_FACTORIES.get(state).create(format);
        }
        return rules;
    }

    private RuleBuilder() {
    }

}
