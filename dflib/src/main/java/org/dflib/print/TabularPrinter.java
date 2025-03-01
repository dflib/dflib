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

    public TabularPrinter(int maxRows, int maxCols, int maxValueChars) {
        super(maxRows, maxCols, maxValueChars);
    }

    @Override
    public void printTo(Appendable sink, DataFrame df) throws IOException {
        new DataFrameTabularAppendable(sink, maxRows, maxCols, maxValueChars).print(df);
    }

    @Override
    public void printTo(Appendable sink, Series<?> s) throws IOException {
        new SeriesTabularAppendable(sink, maxRows, maxValueChars).print(s);
    }
}
