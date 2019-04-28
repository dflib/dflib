package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.row.RowProxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DataFrameTabularPrintWorker extends BasePrintWorker {

    public DataFrameTabularPrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWith) {
        super(out, maxDisplayRows, maxDisplayColumnWith);
    }

    public StringBuilder print(DataFrame df) {

        Index columns = df.getColumnsIndex();
        Iterator<RowProxy> values = df.iterator();

        int w = columns.size();
        if (w == 0) {
            return out;
        }

        String[] labels = columns.getLabels();

        int[] columnWidth = new int[w];
        String[] columnFormat = new String[w];

        List<String[]> data = new ArrayList<>();

        for (int i = 0; i < w; i++) {
            columnWidth[i] = labels[i].length();
        }

        for (int i = 0; i < maxDisplayRows; i++) {
            if (!values.hasNext()) {
                break;
            }

            RowProxy r = values.next();
            String[] rValue = new String[w];

            for (int j = 0; j < w; j++) {
                rValue[j] = String.valueOf(r.get(j));
                columnWidth[j] = Math.max(columnWidth[j], rValue[j].length());
            }

            data.add(rValue);
        }

        // since tabular printer is multiline, start with a line break to ensure logger-induced prefixes don't break
        // table alignment
        appendNewLine();

        // constrain column width and calculate formatters
        for (int i = 0; i < w; i++) {
            columnWidth[i] = Math.min(columnWidth[i], maxDisplayColumnWidth);
            columnFormat[i] = columnFormat(columnWidth[i], df.getColumn(i).getType());
        }

        // print header
        for (int i = 0; i < w; i++) {
            if (i > 0) {
                append(" ");
            }
            appendFixedWidth(labels[i], columnWidth[i], columnFormat[i]);
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
                appendFixedWidth(row[i], columnWidth[i], columnFormat[i]);
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
}
