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

    /**
     * Notifies the context that the current row has more values than the header.
     */
    void markExtraValues();

    // only used by the ColumnEndQuoted rules, could be potentially removed in the future
    DataSlice activeSlice();
}
