package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.row.RowProxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class TabularPrinterWorker extends BasePrinterWorker {

    TabularPrinterWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWith) {
        super(out, maxDisplayRows, maxDisplayColumnWith);
    }

    StringBuilder print(DataFrame df) {

        Index columns = df.getColumnsIndex();
        Iterator<RowProxy> values = df.iterator();

        int w = columns.size();
        if (w == 0) {
            return out;
        }

        String[] labels = columns.getLabels();

        int[] columnWidth = new int[w];
        List<String[]> data = new ArrayList<>();

        for (int i = 0; i < w; i++) {
            columnWidth[i] = labels[i].length();
        }

        for (int i = 0; i < maxDisplayRows; i++) {
            if (!values.hasNext()) {
                break;
            }

            RowProxy dr = values.next();
            String[] drValue = new String[w];

            for (int j = 0; j < w; j++) {
                drValue[j] = String.valueOf(dr.get(j));
                columnWidth[j] = Math.max(columnWidth[j], drValue[j].length());
            }

            data.add(drValue);
        }

        // constrain column width
        for (int i = 0; i < w; i++) {
            columnWidth[i] = Math.min(columnWidth[i], maxDisplayColumnWith);
        }

        // print header
        for (int i = 0; i < w; i++) {
            if (i > 0) {
                append(" ");
            }
            appendFixedWidth(labels[i], columnWidth[i]);
        }

        // print header separator
        appendNewLine();
        for (int i = 0; i < w; i++) {
            if (i > 0) {
                append(" ");
            }

            for (int j = 0; j < columnWidth[i]; j++) {
                append("-");
            }
        }

        // print data
        for (String[] row : data) {
            appendNewLine();
            for (int i = 0; i < w; i++) {
                if (i > 0) {
                    append(" ");
                }
                appendFixedWidth(row[i], columnWidth[i]);
            }
        }

        if (values.hasNext()) {
            appendNewLine().append("...");
        }

        int h = df.height();

        String rowsLabel = h == 1 ? " row x " : " rows x ";
        String columnsLabel = w == 1 ? " column" : " columns";
        appendNewLine().append(h).append(rowsLabel).append(w).append(columnsLabel);

        return out;
    }

    StringBuilder append(String string) {
        return out.append(string);
    }

    StringBuilder appendFixedWidth(String value, int width) {

        if (value.length() <= width) {
            return out.append(String.format("%1$-" + width + "s", value));
        } else {
            return out.append(truncate(value, width));
        }
    }

    private StringBuilder appendNewLine() {
        return out.append(System.lineSeparator());
    }
}
