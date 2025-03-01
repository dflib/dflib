package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.row.RowProxy;

import java.io.IOException;

class DataFrameInlineAppendable extends InlineAppendable {

    public DataFrameInlineAppendable(Appendable out, int maxDisplayRows, int maxDisplayColumnWith) {
        super(out, maxDisplayRows, maxDisplayColumnWith);
    }

    public void print(DataFrame df) throws IOException {

        if (df == null) {
            out.append("null");
            return;
        }

        DataFrameTruncator truncator = DataFrameTruncator.create(df, maxDisplayRows);

        // if no data, print column labels once
        if (truncator.height() == 0) {
            printColumnLabels(df.getColumnsIndex());
            return;
        }

        printData(truncator.top(), false);

        if (truncator.isTruncated()) {
            printRowsSeparator();
        }

        printData(truncator.bottom(), true);
    }

    private void printColumnLabels(Index columns) throws IOException {
        int w = columns.size();
        for (int j = 0; j < w; j++) {

            if (j > 0) {
                out.append(",");
            }

            appendTruncate(columns.get(j));
            out.append(":");
        }
    }

    private void printRowsSeparator() throws IOException {
        out.append(",...");
    }

    private void printData(DataFrame data, boolean startWithComma) throws IOException {
        Index columns = data.getColumnsIndex();
        int w = columns.size();

        boolean comma = startWithComma;
        for (RowProxy p : data) {
            if (comma) {
                out.append(",{");
            } else {
                comma = true;
                out.append("{");
            }

            for (int j = 0; j < w; j++) {

                if (j > 0) {
                    out.append(",");
                }

                appendTruncate(columns.get(j));
                out.append(":");
                appendTruncate(String.valueOf(p.get(j)));
            }

            out.append("}");
        }
    }

}
