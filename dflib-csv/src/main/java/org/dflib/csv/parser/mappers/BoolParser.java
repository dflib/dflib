package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;

final class BoolParser {

    private BoolParser() {
    }

    static boolean parse(DataSlice slice) {
        char[] data = slice.data();
        int from = slice.from();
        int to = slice.to();
        int len = to - from;

        if (len == 1) {
            return data[from] == '1';
        }

        return len == 4 &&
                (data[from    ] == 't' || data[from    ] == 'T') &&
                (data[from + 1] == 'r' || data[from + 1] == 'R') &&
                (data[from + 2] == 'u' || data[from + 2] == 'U') &&
                (data[from + 3] == 'e' || data[from + 3] == 'E');
    }

}
