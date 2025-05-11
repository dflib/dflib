package org.dflib.print;

import java.util.Arrays;

@FunctionalInterface
interface CellFormatter {

    static CellFormatter leftPad(int w) {
        return s -> {

            String unpadded = s != null ? s : "null";
            int ul = unpadded.length();
            if (ul >= w) {
                return unpadded;
            }

            char[] padded = new char[w];
            int pl = w - ul;

            // padding
            Arrays.fill(padded, 0, pl, ' ');

            // original chars
            unpadded.getChars(0, ul, padded, pl);
            return new String(padded);
        };
    }

    static CellFormatter rightPad(int w) {
        return s -> {

            String unpadded = s != null ? s : "null";
            int ul = unpadded.length();
            if (ul >= w) {
                return unpadded;
            }

            char[] padded = new char[w];

            // original chars
            unpadded.getChars(0, ul, padded, 0);

            // padding
            Arrays.fill(padded, ul, w, ' ');

            return new String(padded);
        };
    }

    String format(String s);
}
