package org.dflib.csv.parser;

import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvColumnFormat;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.csv.parser.rules.ParserRule;
import org.dflib.csv.parser.rules.RuleBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages the parser state flow between {@link ParserRule} executions.
 *
 * <p>Rules are responsible for consuming characters and emitting row/column events. This class
 * only decides which rule should run next. The flow progresses through three explicit phases:
 *
 * <h4>1. OFFSET</h4>
 * <p>
 * Enabled only if {@code format.offset() > 0}, just skips first N rows and switches to the AUTO mode.
 *
 * <h4>2. AUTO</h4>
 * <p>
 * Column discovery state, reads the first row of the CSV to determine actual columns present.
 * <p>
 * This mode just keeps parsing repeated {@code START_ANY -> DELIMITER_OR_END_OF_LINE} cycles.
 *
 * <h4>3. COLUMN</h4>
 * <p>
 * This is the main state, parsing is based on a fast per-column state flow. Tries to optimize rule transition based on
 * the columns format (e.g., skipping quotes if quotes are explicitly set to NONE).
 *
 * <h4>State Overrides</h3>
 * <p>In AUTO and COLUMN modes rules may request a specific next state via {@code context.overrideState(...)}
 * to branch from the fixed flow based on the actual content.
 */
final class ParserRuleFlow {

    /**
     * Current state that this flow is currently in
     */
    private enum FlowState {
        /**
         * Skipping initial full rows, controlled by the format.offset() parameter
         */
        OFFSET,
        /**
         * Column discovery state, reads the first row of the CSV to determine actual columns present
         */
        AUTO,
        /**
         * This is the main state, parsing is based on a fast per-column state flow
         */
        COLUMN
    }

    // dependencies
    final CsvParserConfig config;
    final ParserContext context;

    // rules
    final int[] autoFlow;
    final ParserRule[] parserRules;
    ParserRule[] columnFlowRules;

    // state
    private FlowState state;
    private int offsetRemaining;
    private final ParserRule skipRowRule;

    ParserRuleFlow(CsvParserConfig config, ParserContext context) {
        this.config = config;
        this.context = context;
        this.parserRules = RuleBuilder.buildRules(config.csvFormat());
        this.autoFlow = buildAutoFlow(config.csvFormat());
        this.skipRowRule = parserRules[ParserState.SKIP_ROW.ordinal()];

        if (config.offset() > 0) {
            this.state = FlowState.OFFSET;
            this.offsetRemaining = config.offset();
        } else {
            this.state = FlowState.AUTO;
        }
    }

    /**
     * Initializes fixed-column flow and switches rule progression strategy.
     * @param columns column format definitions
     */
    void initColumns(List<CsvColumnMapping> columns) {
        this.columnFlowRules = buildColumnFlowRules(columns);
        this.state = FlowState.COLUMN;
    }

    /**
     * Returns the first rule to execute for the current flow state.
     * @return initial parser rule
     */
    ParserRule initialRule() {
        return switch (state) {
            case OFFSET -> skipRowRule;
            case COLUMN -> {
                context.flowIndex = 0;
                yield columnFlowRules[0];
            }
            case AUTO -> {
                ParserState currentState = initialAutoState(config.csvFormat());
                context.setCurrentState(currentState.ordinal());
                yield parserRules[currentState.ordinal()];
            }
        };
    }

    /**
     * Returns the next rule according to the active flow state.
     * @return next parser rule
     */
    ParserRule nextRule() {
        return switch (state) {
            case OFFSET -> {
                // Switch to the AUTO once the offset is done.
                if (--offsetRemaining <= 0) {
                    state = FlowState.AUTO;
                    yield initialRule();
                }
                yield skipRowRule;
            }
            case AUTO -> {
                if (context.stateOverride == 0) {
                    context.setCurrentState(autoFlow[context.currentState()]);
                    yield parserRules[context.currentState()];
                } else {
                    yield parserRules[context.nextStateOverride()];
                }
            }
            case COLUMN -> {
                if (context.stateOverride == 0) {
                    yield columnFlowRules[++context.flowIndex];
                } else {
                    yield parserRules[context.nextStateOverride()];
                }
            }
        };
    }

    /**
     * Builds the state sequence for column-flow mode.
     * @param columns column format definitions
     * @return ordered parser states representing one full row in column-flow mode
     */
    private ParserState[] buildDefaultStateFlow(List<CsvColumnMapping> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified. " +
                    "Please specify at least one column or use `.autoColumns(true)` for auto-detection.");
        }

        List<ParserState> states = new ArrayList<>();
        if (config.csvFormat().skipEmptyRows()) {
            states.add(ParserState.EMPTY_ROW);
        }
        if (config.csvFormat().comment() != null) {
            states.add(ParserState.COMMENT);
        }
        for (CsvColumnMapping columnFormat : columns) {
            if (columnFormat.format().quote().optional()) {
                states.add(ParserState.START_ANY);
            } else if (columnFormat.format().quote().noQuote()) {
                states.add(ParserState.START_UNQUOTED);
            } else {
                states.add(ParserState.START_QUOTED);
                states.add(ParserState.END_QUOTED);
            }
            states.add(config.csvFormat().allowEmptyColumns()
                    ? ParserState.DELIMITER_OR_END_OF_LINE
                    : ParserState.DELIMITER);
        }
        // replace the last delimiter with END_OF_LINE or trailing delimiter
        if (config.csvFormat().trailingDelimiter()) {
            states.set(states.size() - 1, ParserState.DELIMITER_OR_END_OF_LINE);
            states.add(ParserState.END_OF_LINE);
        } else {
            states.set(states.size() - 1, ParserState.END_OF_LINE);
        }
        return states.toArray(new ParserState[0]);
    }

    /**
     * Resolves the initial auto-flow state from format flags.
     * @param format CSV format
     * @return first auto-flow state
     */
    private static ParserState initialAutoState(CsvFormat format) {
        if (format.skipEmptyRows()) {
            return ParserState.EMPTY_ROW;
        } else if (format.comment() != null) {
            return ParserState.COMMENT;
        } else {
            return ParserState.START_ANY;
        }
    }

    /**
     * Builds transition table for auto-flow mode.
     * @param format CSV format
     * @return auto-flow transition table indexed by {@link ParserState#ordinal()}
     */
    private static int[] buildAutoFlow(CsvFormat format) {
        int[] table = new int[ParserState.values().length];
        Arrays.fill(table, ParserState.START_ANY.ordinal());
        table[ParserState.NO.ordinal()] = initialAutoState(format).ordinal();
        table[ParserState.EMPTY_ROW.ordinal()] = format.comment() != null
                ? ParserState.COMMENT.ordinal()
                : ParserState.START_ANY.ordinal();
        table[ParserState.COMMENT.ordinal()] = ParserState.START_ANY.ordinal();
        table[ParserState.START_ANY.ordinal()] = ParserState.DELIMITER_OR_END_OF_LINE.ordinal();
        table[ParserState.START_QUOTED_OPTIONAL.ordinal()] = ParserState.DELIMITER_OR_END_OF_LINE.ordinal();
        table[ParserState.START_QUOTED.ordinal()] = ParserState.END_QUOTED.ordinal();
        table[ParserState.END_QUOTED.ordinal()] = ParserState.DELIMITER_OR_END_OF_LINE.ordinal();
        table[ParserState.DELIMITER_OR_END_OF_LINE.ordinal()] = ParserState.START_ANY.ordinal();
        table[ParserState.END_OF_LINE.ordinal()] = initialAutoState(format).ordinal();
        table[ParserState.SKIP_ROW.ordinal()] = ParserState.SKIP_ROW.ordinal();
        return table;
    }

    /**
     * Builds fixed parser rules for column-flow mode.
     * @param columns column format definitions
     * @return prebuilt parser rules for a single row traversal in column-flow mode
     */
    private ParserRule[] buildColumnFlowRules(List<CsvColumnMapping> columns) {
        ParserState[] states = buildDefaultStateFlow(columns);
        ParserRule[] rules = new ParserRule[states.length];
        for (int i = 0; i < states.length; i++) {
            rules[i] = parserRules[states[i].ordinal()];
        }
        return rules;
    }
}
