package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;

public interface Printer {

    /**
     * @since 2.0.0
     */
    default String print(Series<?> s) {
        return print(new StringBuilder(), s).toString();
    }

    /**
     * @since 2.0.0
     */
    default String print(DataFrame df) {
        return print(new StringBuilder(), df).toString();
    }

    /**
     * @deprecated in favor of {@link #print(Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default String toString(Series<?> s) {
        return print(s);
    }

    /**
     * @deprecated in favor of {@link #print(DataFrame)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default String toString(DataFrame df) {
        return print(df);
    }

    StringBuilder print(StringBuilder out, Series<?> s);

    StringBuilder print(StringBuilder out, DataFrame df);
}
