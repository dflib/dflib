package org.dflib.range;

public class Range {

    public static void checkRange(int from, int len, int masterLen) {

        if (from < 0) {
            throw new IllegalArgumentException("Negative 'from' index: " + from);
        }

        if (len < 0) {
            throw new IllegalArgumentException("Negative 'len': " + len);
        }

        if (from > masterLen) {
            throw new IllegalArgumentException("From is out of range: " + from + " (" + masterLen + ")");
        }

        if (from + len > masterLen) {
            throw new IllegalArgumentException("Length is out of range: (" + (from + len) + " > " + masterLen + ")");
        }
    }
}
