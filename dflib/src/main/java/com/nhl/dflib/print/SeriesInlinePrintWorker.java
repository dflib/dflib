package com.nhl.dflib.print;

import com.nhl.dflib.Series;

public class SeriesInlinePrintWorker extends BasePrintWorker {

    public SeriesInlinePrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWidth) {
        super(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    public StringBuilder print(Series<?> s) {

        int w = s.size();
        out.append("{");

        int wp = Math.min(w, maxDisplayRows);

        for (int i = 0; i < wp; i++) {

            if (i > 0) {
                out.append(",");
            }

            appendTruncate(String.valueOf(s.get(i)));
        }

        if (w > wp) {
            out.append(",...");
        }

        out.append("}");

        return out;
    }
}
