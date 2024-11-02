package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.row.RowProxy;

import java.util.ArrayList;
import java.util.List;

public class DataFrameTabularPrintWorker extends BasePrintWorker {

    public DataFrameTabularPrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWith) {
        super(out, maxDisplayRows, maxDisplayColumnWith);
    }

    public StringBuilder print(DataFrame df) {

        if (df == null) {
            out.append("null");
            return out;
        }

        Index columns = df.getColumnsIndex();

        int w = columns.size();
        if (w == 0) {
            return out;
        }

        int[] columnWidth = new int[w];
        String[] columnFormat = new String[w];

        DataFrameTruncator truncator = DataFrameTruncator.create(df, maxDisplayRows);

        for (int i = 0; i < w; i++) {
            columnWidth[i] = columns.get(i).length();
        }

        DataFrame head = truncator.head();
        List<String[]> headData = new ArrayList<>(head.height());
        for (RowProxy p : head) {

            String[] rValue = new String[w];

            for (int i = 0; i < w; i++) {
                rValue[i] = String.valueOf(p.get(i));
                columnWidth[i] = Math.max(columnWidth[i], rValue[i].length());
            }

            headData.add(rValue);
        }

        List<String[]> tailData = null;
        if (truncator.isTruncated()) {

            DataFrame tail = truncator.tail();
            tailData = new ArrayList<>(tail.height());
            for (RowProxy p : tail) {

                String[] rValue = new String[w];

                for (int i = 0; i < w; i++) {
                    rValue[i] = String.valueOf(p.get(i));
                    columnWidth[i] = Math.max(columnWidth[i], rValue[i].length());
                }

                tailData.add(rValue);
            }
        }

        // since tabular printer is multiline, start with a line break to ensure logger-induced prefixes don't break
        // table alignment
        appendNewLine();

        // constrain column width and calculate formatters
        for (int i = 0; i < w; i++) {
            // sometimes we get "0" width for empty columns, need to bump those to 1
            columnWidth[i] = Math.max(1, Math.min(columnWidth[i], maxDisplayColumnWidth));
            columnFormat[i] = columnFormat(columnWidth[i], df.getColumn(i).getInferredType());
        }

        // print header
        for (int i = 0; i < w; i++) {
            if (i > 0) {
                append(" ");
            }
            appendFixedWidth(columns.get(i), columnWidth[i], columnFormat[i]);
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
        for (String[] row : headData) {
            appendNewLine();
            for (int i = 0; i < w; i++) {
                if (i > 0) {
                    append(" ");
                }
                appendFixedWidth(row[i], columnWidth[i], columnFormat[i]);
            }
        }

        if (truncator.isTruncated()) {
            appendNewLine().append("...");

            for (String[] row : tailData) {
                appendNewLine();
                for (int i = 0; i < w; i++) {
                    if (i > 0) {
                        append(" ");
                    }
                    appendFixedWidth(row[i], columnWidth[i], columnFormat[i]);
                }
            }
        }

        int h = df.height();

        String nameLabel = df.getName() != null ? "[" + df.getName() + "] " : "";
        String rowsLabel = h == 1 ? " row x " : " rows x ";
        String columnsLabel = w == 1 ? " column" : " columns";
        appendNewLine()
                .append(nameLabel)
                .append(h).append(rowsLabel)
                .append(w).append(columnsLabel);

        return out;
    }

    StringBuilder append(String string) {
        return out.append(string);
    }
}
