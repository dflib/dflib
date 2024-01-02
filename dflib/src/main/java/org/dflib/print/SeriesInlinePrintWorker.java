package org.dflib.print;

import org.dflib.Series;

public class SeriesInlinePrintWorker extends BasePrintWorker {

    public SeriesInlinePrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWidth) {
        super(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    public StringBuilder print(Series<?> s) {

        if (s == null) {
            out.append("null");
            return out;
        }

        SeriesTruncator truncator = SeriesTruncator.create(s, maxDisplayRows);
        Series<?> head = truncator.head();
        int hs = head.size();

        for (int i = 0; i < hs; i++) {

            if (i > 0) {
                out.append(",");
            }

            appendTruncate(String.valueOf(s.get(i)));
        }

        if(truncator.isTruncated()) {
            out.append(",...");

            Series<?> tail = truncator.tail();
            int ts = tail.size();

            for (int i = 0; i < ts; i++) {
                out.append(",");
                String val = String.valueOf(tail.get(i));
                appendTruncate(val);
            }
        }

        return out;
    }
}
