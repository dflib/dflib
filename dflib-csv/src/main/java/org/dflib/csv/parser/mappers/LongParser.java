package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;

final class LongParser {

    private static final long LIMIT = Long.MAX_VALUE / 10;

    private LongParser() {
    }

    static long parse(DataSlice slice) {
        char[] data = slice.data();
        int from = slice.from();
        int to = slice.to();
        if (from >= to) {
            throw new NumberFormatException("Empty or whitespace-only input");
        }

        long result = 0L;
        boolean minus = false;
        if (data[from] == '-') {
            minus = true;
            from++;
        }

        if (from >= to) {
            throw new NumberFormatException("No digits found");
        }

        for (int i = from; i < to; i++) {
            char c = data[i];
            if (c >= '0' && c <= '9') {
                int digit = c - '0';
                if (result > LIMIT || (result == LIMIT && digit > (minus ? 8 : 7))) {
                    throw new NumberFormatException("Long overflow");
                }
                result = result * 10 + digit;
            } else {
                throw new NumberFormatException("Unexpected character for a numeric value: '" + c + "'");
            }
        }
        return minus ? -result : result;
    }
}
