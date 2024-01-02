package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;

public interface Printer {

    default String toString(Series<?> s) {
        return print(new StringBuilder(), s).toString();
    }

    default String toString(DataFrame df) {
        return print(new StringBuilder(), df).toString();
    }

    StringBuilder print(StringBuilder out, Series<?> s);

    StringBuilder print(StringBuilder out, DataFrame df);
}
