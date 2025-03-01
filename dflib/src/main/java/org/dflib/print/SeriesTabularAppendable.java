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
            appendNewLine();
            out.append("0 elements");
            return;
        }

        SeriesTruncator truncator = SeriesTruncator.create(s, maxDisplayRows);
        Series<?> top = truncator.top();
        Series<?> bottom = truncator.bottom();

        TabularColumnData strings = makeStrings(s, top, bottom);

        int offset = 0;
        int topH = top.size();
        int bottomH = bottom.size();

        // print top
        for (int i = 0; i < topH; i++) {
            appendNewLine();
            strings.printTo(out, offset++);
        }

        // print separator if needed
        if (truncator.isTruncated()) {
            appendNewLine();
            out.append("...");
        }

        // print bottom
        for (int i = 0; i < bottomH; i++) {
            appendNewLine();
            strings.printTo(out, offset++);
        }

        String rowsLabel = h == 1 ? " element" : " elements";
        appendNewLine();
        out.append(Integer.toString(h)).append(rowsLabel);
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
