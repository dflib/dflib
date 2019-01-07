package com.nhl.yadf.print;

import com.nhl.yadf.Index;
import com.nhl.yadf.IndexPosition;

import java.util.Iterator;

public class InlinePrinterWorker extends BasePrinterWorker {

    public InlinePrinterWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWith) {
        super(out, maxDisplayRows, maxDisplayColumnWith);
    }

    @Override
    StringBuilder print(Index columns, Iterator<Object[]> values) {

        int width = columns.size();
        if (width == 0) {
            return out;
        }

        IndexPosition[] positions = columns.getPositions();

        for (int i = 0; i < maxDisplayRows; i++) {
            if (!values.hasNext()) {
                break;
            }

            if (i > 0) {
                out.append(",");
            }

            Object[] dr = values.next();

            out.append("{");
            for (int j = 0; j < width; j++) {

                if (j > 0) {
                    out.append(",");
                }

                appendTruncate(positions[j].name());
                out.append(":");
                appendTruncate(String.valueOf(positions[j].get(dr)));
            }

            out.append("}");
        }

        if (values.hasNext()) {
            out.append(",...");
        }

        return out;
    }

    StringBuilder appendTruncate(String value) {
        return out.append(truncate(value, maxDisplayColumnWith));
    }
}
