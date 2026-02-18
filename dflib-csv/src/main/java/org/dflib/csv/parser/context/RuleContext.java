package org.dflib.csv.parser.context;

import org.dflib.csv.parser.ParserState;

/**
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 * @since 2.0.0
 */
public interface RuleContext {

    void markColumnStart(int position);

    void markColumnStartQuoted(int position);

    void markColumnEnd(int position, boolean optional);

    void markRowEnd(int position);

    void overrideState(ParserState parserState);

    void skipRow(int position);

    void markColumnEscape();

    boolean isLastSliceQuoted();

    // only used by the ColumnEndQuoted rules, could be potentially removed in the future
    DataSlice activeSlice();
}
