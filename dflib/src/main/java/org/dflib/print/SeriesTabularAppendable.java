package org.dflib.print;

import org.dflib.Series;

import java.io.IOException;

class SeriesTabularAppendable extends TabularAppendable {

    public SeriesTabularAppendable(Appendable out, int maxDisplayRows, int maxDisplayColumnWidth) {
        super(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    public void print(Series<?> s) throws IOException {

        if (s == null) {
            out.append("null");
            return;
        }

        int h = s.size();
        if (h == 0) {
            printNewLine();
            out.append("0 elements");
            return;
        }

        SeriesTruncator truncator = SeriesTruncator.create(s, maxDisplayRows);
        Series<?> top = truncator.top();
        Series<?> bottom = truncator.bottom();
        TabularColumnData strings = makeStrings(s, top, bottom);

        printData(strings, 0, top.size());
        if (truncator.isTruncated()) {
            printRowsSeparator();
        }
        printData(strings, top.size(), bottom.size());

        String rowsLabel = h == 1 ? " element" : " elements";
        printNewLine();
        out.append(Integer.toString(h)).append(rowsLabel);
    }

    private void printRowsSeparator() throws IOException {
        printNewLine();
        out.append("...");
    }

    private void printData(TabularColumnData strings, int offset, int len) throws IOException {
        for (int i = 0; i < len; i++) {
            printNewLine();
            strings.printTo(out, offset++);
        }
    }

    private TabularColumnData makeStrings(Series<?> s, Series<?> top, Series<?> bottom) {

        int workerH = top.size() + bottom.size();

        TabularColumnData.Builder builder = TabularColumnData
                .builder(s.getInferredType(), workerH, maxDisplayColumnWidth);

        for (Object o : top) {
            builder.append(o);
        }

        for (Object o : bottom) {
            builder.append(o);
        }

        return builder.build();
    }
}
