package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;

public interface Printer {

    default String toString(DataFrame df) {
        return print(new StringBuilder(), df).toString();
    }

    StringBuilder print(StringBuilder out, DataFrame df);
}
