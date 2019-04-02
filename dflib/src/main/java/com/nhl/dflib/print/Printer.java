package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

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
