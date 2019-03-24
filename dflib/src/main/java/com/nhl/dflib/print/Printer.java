package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;

public interface Printer {

    default String toString(DataFrame df) {
        return append(df, new StringBuilder()).toString();
    }

    StringBuilder append(DataFrame df, StringBuilder out);
}
