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

    public InlinePrinter(int maxRows, int maxCols, int maxValueChars) {
        super(maxRows, maxCols, maxValueChars);
    }

    @Override
    public void printTo(Appendable sink, Series<?> s) throws IOException {
        new SeriesInlineAppendable(sink, maxRows, maxValueChars).print(s);
    }

    @Override
    public void printTo(Appendable sink, DataFrame df) throws IOException {
        new DataFrameInlineAppendable(sink, maxRows, maxCols, maxValueChars).print(df);
    }
}
