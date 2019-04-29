package com.nhl.dflib.range;

/**
 * @since 0.6
 */
// TODO: start using ranges within Series, Indexes, etc.
public class Range {

    public static void checkRange(int from, int len, int masterLen) {
        // TODO: allow negative ranges to reference range from the tail?
        if (from < 0) {
            throw new IllegalArgumentException("Negative 'from' index: " + from);
        }

        if (from > masterLen) {
            throw new IllegalArgumentException("From is out of range: " + from + " (" + masterLen + ")");
        }

        if (from + len > masterLen) {
            throw new IllegalArgumentException("Length is out of range: " + (from + len) + " > " + masterLen + ")");
        }
    }
}
