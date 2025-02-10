package org.dflib.print;

import org.dflib.Series;

public class SeriesTabularPrintWorker extends BasePrintWorker {

    public SeriesTabularPrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWidth) {
        super(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    public StringBuilder print(Series<?> s) {

        if (s == null) {
            out.append("null");
            return out;
        }

        int h = s.size();
        if (h == 0) {
            appendNewLine().append("0 elements");
            return out;
        }

        int columnWidth = 0;
        String columnFormat;

        SeriesTruncator truncator = SeriesTruncator.create(s, maxDisplayRows);

        // need to accumulate all values to calculate column width
        String[] values = new String[truncator.size()];

        Series<?> head = truncator.head();
        int hs = head.size();

        for (int i = 0; i < hs; i++) {
            values[i] = String.valueOf(head.get(i));
            columnWidth = Math.max(columnWidth, values[i].length());
        }

        if (truncator.isTruncated()) {
            values[hs] = "...";

            Series<?> tail = truncator.tail();
            int ts = tail.size();

            for (int i = 0; i < ts; i++) {
                String val = String.valueOf(tail.get(i));
                values[hs + 1 + i] = val;
                columnWidth = Math.max(columnWidth, val.length());
            }
        }

        // constrain column width
        // sometimes we get "0" width for empty columns, need to bump those to 1
        columnWidth = Math.max(1, Math.min(columnWidth, maxDisplayColumnWidth));
        columnFormat = columnFormat(columnWidth, s.getInferredType());

        // since tabular printer is multiline, start with a line break to ensure logger-induced prefixes don't break
        // table alignment
        appendNewLine();

        // print data
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                appendNewLine();
            }
            appendFixedWidth(values[i], columnWidth, columnFormat);
        }

        String rowsLabel = h == 1 ? " element" : " elements";
        appendNewLine().append(h).append(rowsLabel);

        return out;
    }
}
