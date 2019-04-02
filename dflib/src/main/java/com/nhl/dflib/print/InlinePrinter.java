package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

/**
 * A utility class for outputting DataFrames and DataRows on a single line.
 */
public class InlinePrinter extends BasePrinter {

    public InlinePrinter() {
    }

    public InlinePrinter(int maxDisplayRows, int maxDisplayColumnWith) {
        super(maxDisplayRows, maxDisplayColumnWith);
    }

    @Override
    public StringBuilder print(StringBuilder out, Series<?> s) {
        return new SeriesInlinePrintWorker(out, maxDisplayRows, maxDisplayColumnWidth).print(s);
    }

    @Override
    public StringBuilder print(StringBuilder out, DataFrame df) {
        return new DataFrameInlinePrintWorker(out, maxDisplayRows, maxDisplayColumnWidth).print(df);
    }
}
