package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;

import java.io.IOException;

/**
 * A utility class for outputting DataFrames and DataRows on a single line.
 */
public class InlinePrinter extends BasePrinter {

    public InlinePrinter() {
    }

    public InlinePrinter(int maxDisplayRows, int maxDisplayColumnWidth) {
        super(maxDisplayRows, maxDisplayColumnWidth);
    }

    @Override
    public void printTo(Appendable sink, Series<?> s) throws IOException {
        new SeriesInlinePrintWorker(sink, maxDisplayRows, maxDisplayColumnWidth).print(s);
    }

    @Override
    public void printTo(Appendable sink, DataFrame df) throws IOException {
        new DataFrameInlinePrintWorker(sink, maxDisplayRows, maxDisplayColumnWidth).print(df);
    }
}
