package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.row.RowProxy;

import java.util.Iterator;

public class DataFrameInlinePrintWorker extends DataFramePrintWorker {

    public DataFrameInlinePrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWith) {
        super(out, maxDisplayRows, maxDisplayColumnWith);
    }

    @Override
    StringBuilder print(DataFrame df) {

        Index columns = df.getColumnsIndex();
        Iterator<RowProxy> values = df.iterator();

        int width = columns.size();
        if (width == 0) {
            return out;
        }

        String[] labels = columns.getLabels();

        for (int i = 0; i < maxDisplayRows; i++) {
            if (!values.hasNext()) {
                break;
            }

            if (i > 0) {
                out.append(",");
            }

            RowProxy dr = values.next();

            out.append("{");
            for (int j = 0; j < width; j++) {

                if (j > 0) {
                    out.append(",");
                }

                appendTruncate(labels[j]);
                out.append(":");
                appendTruncate(String.valueOf(dr.get(j)));
            }

            out.append("}");
        }

        if (values.hasNext()) {
            out.append(",...");
        }

        return out;
    }

    StringBuilder appendTruncate(String value) {
        return out.append(truncate(value, maxDisplayColumnWidth));
    }
}
