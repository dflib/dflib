package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;

/**
 * A utility class for outputting DataFrames and DataRows as constrained tables.
 */
public class TabularPrinter extends BasePrinter {

    public TabularPrinter() {
    }

    public TabularPrinter(int maxDisplayRows, int maxDisplayColumnWidth) {
        super(maxDisplayRows, maxDisplayColumnWidth);
    }

    @Override
    public StringBuilder print(StringBuilder out, DataFrame df) {
        return new DataFrameTabularPrintWorker(out, maxDisplayRows, maxDisplayColumnWidth).print(df);
    }

    @Override
    public StringBuilder print(StringBuilder out, Series<?> s) {
        return new SeriesTabularPrintWorker(out, maxDisplayRows, maxDisplayColumnWidth).print(s);
    }
}
