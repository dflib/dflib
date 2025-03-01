package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;

import java.io.IOException;

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
    public void printTo(Appendable sink, DataFrame df) throws IOException {
        new DataFrameTabularAppendable(sink, maxDisplayRows, maxDisplayColumnWidth).print(df);
    }

    @Override
    public void printTo(Appendable sink, Series<?> s) throws IOException {
        new SeriesTabularAppendable(sink, maxDisplayRows, maxDisplayColumnWidth).print(s);
    }
}
