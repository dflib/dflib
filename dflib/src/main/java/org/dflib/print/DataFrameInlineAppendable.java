package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.row.RowProxy;

import java.io.IOException;

class DataFrameInlineAppendable extends InlineAppendable {

    private final int maxCols;

    public DataFrameInlineAppendable(Appendable out, int maxRows, int maxCols, int maxValueChars) {
        super(out, maxRows, maxValueChars);
        this.maxCols = maxCols;
    }

    public void print(DataFrame df) throws IOException {

        if (df == null) {
            out.append("null");
            return;
        }

        SeriesTruncator<String> colTruncator = SeriesTruncator.create(df.getColumnsIndex(), maxCols);

        // if no data, print column labels once
        if (df.height() == 0) {
            printColumnLabels(colTruncator);
            return;
        }

        DataFrameTruncator rowTruncator = DataFrameTruncator.create(df, maxRows);
        printData(rowTruncator.head, false, colTruncator);
        if (rowTruncator.truncated) {
            printRowsSeparator();
        }

        printData(rowTruncator.tail, true, colTruncator);
    }

    private void printColumnLabels(SeriesTruncator<String> colTruncator) throws IOException {
        int lw = colTruncator.head.size();
        int rw = colTruncator.tail.size();

        for (int i = 0; i < lw; i++) {

            if (i > 0) {
                out.append(",");
            }

            printTruncate(colTruncator.head.get(i));
            out.append(":");
        }

        if (colTruncator.truncated) {
            out.append(",...");
        }

        for (int i = 0; i < rw; i++) {
            out.append(",");
            printTruncate(colTruncator.tail.get(i));
            out.append(":");
        }
    }

    private void printRowsSeparator() throws IOException {
        out.append(",...");
    }

    private void printData(DataFrame df, boolean startWithComma, SeriesTruncator<String> colTruncator) throws IOException {

        int lw = colTruncator.head.size();
        int rw = colTruncator.tail.size();

        int[] rightPositions = df.getColumnsIndex().positions(colTruncator.tail.toArray(new String[0]));

        boolean comma = startWithComma;
        for (RowProxy p : df) {
            if (comma) {
                out.append(",{");
            } else {
                comma = true;
                out.append("{");
            }

            for (int j = 0; j < lw; j++) {

                if (j > 0) {
                    out.append(",");
                }

                printTruncate(colTruncator.head.get(j));
                out.append(":");
                printTruncate(String.valueOf(p.get(j)));
            }

            if (colTruncator.truncated) {
                out.append(",...");
            }

            for (int j = 0; j < rw; j++) {

                out.append(",");

                printTruncate(colTruncator.tail.get(j));
                out.append(":");
                printTruncate(String.valueOf(p.get(rightPositions[j])));
            }

            out.append("}");
        }
    }

}
