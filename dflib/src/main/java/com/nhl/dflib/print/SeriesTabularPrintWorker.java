package com.nhl.dflib.print;

import com.nhl.dflib.Series;

public class SeriesTabularPrintWorker extends BasePrintWorker {

    public SeriesTabularPrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWidth) {
        super(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    public StringBuilder print(Series<?> s) {

        int w = s.size();
        if (w == 0) {
            return out;
        }

        int columnWidth = 0;
        String columnFormat;
        String[] values = new String[w];
        int pw = Math.min(w, maxDisplayRows);

        for (int i = 0; i < pw; i++) {
            values[i] = String.valueOf(s.get(i));
            columnWidth = Math.max(columnWidth, values[i].length());
        }

        // constrain column width
        columnWidth = Math.min(columnWidth, maxDisplayColumnWidth);
        columnFormat = columnFormat(columnWidth, s.getType());

        // since tabular printer is multiline, start with a line break to ensure logger-induced prefixes don't break
        // table alignment
        appendNewLine();

        // print data
        for (int i = 0; i < pw; i++) {
            if (i > 0) {
                appendNewLine();
            }
            appendFixedWidth(values[i], columnWidth, columnFormat);
        }

        if (pw < w) {
            appendNewLine().append("...");
        }

        String rowsLabel = w == 1 ? " row" : " rows";
        appendNewLine().append(w).append(rowsLabel);

        return out;
    }
}
