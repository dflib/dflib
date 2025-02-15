package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;

import java.io.IOException;

public interface Printer {

    /**
     * Returns a String representation of Series with format and truncation based on this printer parameters.
     *
     * @since 2.0.0
     */
    default String print(Series<?> s) {
        StringBuilder out = new StringBuilder();

        try {
            printTo(out, s);
        } catch (IOException e) {
            throw new RuntimeException("Error printing DataFrame", e);
        }

        return out.toString();
    }

    /**
     * Returns a String representation of DataFrame with format and truncation based on this printer parameters.
     *
     * @since 2.0.0
     */
    default String print(DataFrame df) {
        StringBuilder out = new StringBuilder();

        try {
            printTo(out, df);
        } catch (IOException e) {
            throw new RuntimeException("Error printing DataFrame", e);
        }
        return out.toString();
    }

    /**
     * Prints Series data into the provided sink with format and truncation based on this printer parameters.
     *
     * @since 2.0.0
     */
    void printTo(Appendable sink, Series<?> s) throws IOException;

    /**
     * Prints DataFrame data into the provided sink with format and truncation based on this printer parameters.
     *
     * @since 2.0.0
     */
    void printTo(Appendable sink, DataFrame df) throws IOException;

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

    /**
     * @deprecated in favor of {@link #printTo(Appendable, Series)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default StringBuilder print(StringBuilder out, Series<?> s) {
        return out.append(print(s));
    }

    /**
     * @deprecated in favor of {@link #printTo(Appendable, DataFrame)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    default StringBuilder print(StringBuilder out, DataFrame df) {
        return out.append(print(df));
    }
}
