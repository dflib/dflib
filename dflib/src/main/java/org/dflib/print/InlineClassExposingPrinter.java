package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;

import java.io.IOException;

public class InlineClassExposingPrinter extends InlinePrinter {

    @Override
    public void printTo(Appendable sink, Series<?> s) throws IOException {

        if (s == null) {
            sink.append("null");
            return;
        }

        String name = s.getClass().getSimpleName();
        sink.append(name).append(" [");
        super.printTo(sink, s);
        sink.append("]");
    }

    @Override
    public void printTo(Appendable sink, DataFrame df) throws IOException {
        if (df == null) {
            sink.append("null");
            return;
        }

        String name = df.getClass().getSimpleName();
        sink.append(name).append(" [");
        super.printTo(sink, df);
        sink.append("]");
    }
}
