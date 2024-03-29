package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.row.RowProxy;

public class DataFrameInlinePrintWorker extends BasePrintWorker {

    public DataFrameInlinePrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWith) {
        super(out, maxDisplayRows, maxDisplayColumnWith);
    }

    public StringBuilder print(DataFrame df) {

        if (df == null) {
            out.append("null");
            return out;
        }

        DataFrameTruncator truncator = DataFrameTruncator.create(df, maxDisplayRows);

        Index columns = df.getColumnsIndex();
        int width = columns.size();
        int h = truncator.height();

        // if no data, print column labels once
        if (h == 0) {
            for (int j = 0; j < width; j++) {

                if (j > 0) {
                    out.append(",");
                }

                appendTruncate(columns.get(j));
                out.append(":");
            }

            return out;
        }

        boolean comma = false;
        for (RowProxy p : truncator.head()) {

            if (comma) {
                out.append(",");
            }

            comma = true;

            out.append("{");
            for (int j = 0; j < width; j++) {

                if (j > 0) {
                    out.append(",");
                }

                appendTruncate(columns.get(j));
                out.append(":");
                appendTruncate(String.valueOf(p.get(j)));
            }

            out.append("}");
        }

        if (truncator.isTruncated()) {
            if (comma) {
                out.append(",");
            }

            out.append("...");

            for (RowProxy p : truncator.tail()) {
                out.append(",{");
                for (int j = 0; j < width; j++) {

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

        return out;
    }

}
