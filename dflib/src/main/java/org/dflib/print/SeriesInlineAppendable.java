package org.dflib.print;

import org.dflib.Series;

import java.io.IOException;

public class SeriesInlineAppendable extends InlineAppendable {

    public SeriesInlineAppendable(Appendable out, int maxRows, int maxValueChars) {
        super(out, maxRows, maxValueChars);
    }

    public void print(Series<?> s) throws IOException {

        if (s == null) {
            out.append("null");
            return;
        }

        SeriesTruncator<?> truncator = SeriesTruncator.create(s, maxRows);

        printData(truncator.head, false);

        if (truncator.truncated) {
            printRowsSeparator();
        }

        printData(truncator.tail, true);
    }

    private void printRowsSeparator() throws IOException {
        out.append(",...");
    }

    private void printData(Series<?> data, boolean startWithComma) throws IOException {

        boolean comma = startWithComma;
        for (Object o : data) {
            if (comma) {
                out.append(",");
            } else {
                comma = true;
            }

            printTruncate(String.valueOf(o));
        }
    }
}
