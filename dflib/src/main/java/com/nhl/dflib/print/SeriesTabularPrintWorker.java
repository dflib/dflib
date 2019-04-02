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

        int[] columnWidth = new int[w];
        String[] values = new String[w];
        int pw = Math.min(w, maxDisplayRows);

        for (int i = 0; i < pw; i++) {
            values[i] = String.valueOf(s.get(i));
            columnWidth[i] = Math.max(columnWidth[i], values[i].length());
        }

        // constrain column width
        for (int i = 0; i < pw; i++) {
            columnWidth[i] = Math.min(columnWidth[i], maxDisplayColumnWidth);
        }

        // print data
        for (int i = 0; i < pw; i++) {
            if (i > 0) {
                appendNewLine();
            }
            appendFixedWidth(values[i], columnWidth[i]);
        }

        if (pw < w) {
            appendNewLine().append("...");
        }

        String rowsLabel = w == 1 ? " row" : " rows";
        appendNewLine().append(w).append(rowsLabel);

        return out;
    }
}
