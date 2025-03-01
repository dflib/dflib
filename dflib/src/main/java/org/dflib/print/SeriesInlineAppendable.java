package org.dflib.print;

import org.dflib.Series;

import java.io.IOException;

public class SeriesInlineAppendable extends InlineAppendable {

    public SeriesInlineAppendable(Appendable out, int maxDisplayRows, int maxDisplayColumnWidth) {
        super(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    public void print(Series<?> s) throws IOException {

        if (s == null) {
            out.append("null");
            return;
        }

        SeriesTruncator truncator = SeriesTruncator.create(s, maxDisplayRows);

        printData(truncator.top(), false);

        if (truncator.isTruncated()) {
            printRowsSeparator();
        }

        printData(truncator.bottom(), true);
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

            appendTruncate(String.valueOf(o));
        }
    }
}
