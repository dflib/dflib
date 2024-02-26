package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;

/**
 * @since 1.0.0-M20
 */
public class InlineClassExposingPrinter extends InlinePrinter {

    @Override
    public StringBuilder print(StringBuilder out, Series<?> s) {

        if (s == null) {
            return out.append("null");
        }

        String name = s.getClass().getSimpleName();
        out.append(name).append(" [");
        super.print(out, s);
        return out.append("]");
    }

    @Override
    public StringBuilder print(StringBuilder out, DataFrame df) {
        if (df == null) {
            return out.append("null");
        }

        String name = df.getClass().getSimpleName();
        out.append(name).append(" [");
        super.print(out, df);
        return out.append("]");
    }
}
