package org.dflib.agg;

import org.dflib.Series;

/**
 * @since 2.0.0
 */
public class VConcatAgg {

    public static String ofStrings(Series<?> s, String delimiter) {
        return ofStrings(s, delimiter, null, null);
    }

    public static String ofStrings(Series<?> s, String delimiter, String prefix, String suffix) {

        StringBuilder out = new StringBuilder();

        if (prefix != null) {
            out.append(prefix);
        }

        int size = s.size();
        if (size > 0) {
            out.append(s.get(0));

            for (int i = 1; i < size; i++) {
                out.append(delimiter).append(s.get(i));
            }
        }

        if (suffix != null) {
            out.append(suffix);
        }

        return out.toString();
    }
}
