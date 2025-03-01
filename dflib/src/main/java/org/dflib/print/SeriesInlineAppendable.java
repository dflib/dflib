package org.dflib.print;

import org.dflib.Series;

import java.io.IOException;

public class SeriesInlineAppendable extends InlineAppendable {

    public SeriesInlineAppendable(Appendable out, int maxDisplayRows, int maxDisplayColumnWidth) {
        super(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    public void print(Series<?> s) throws IOException {

        if (s == null) {
            out.append("null");
            return;
        }

        SeriesTruncator truncator = SeriesTruncator.create(s, maxDisplayRows);
        Series<?> head = truncator.top();
        int hs = head.size();

        for (int i = 0; i < hs; i++) {

            if (i > 0) {
                out.append(",");
            }

            appendTruncate(String.valueOf(s.get(i)));
        }

        if(truncator.isTruncated()) {
            out.append(",...");

            Series<?> tail = truncator.bottom();
            int ts = tail.size();

            for (int i = 0; i < ts; i++) {
                out.append(",");
                String val = String.valueOf(tail.get(i));
                appendTruncate(val);
            }
        }
    }
}
