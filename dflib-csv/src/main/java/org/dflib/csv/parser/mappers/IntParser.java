package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;

final class IntParser {

    private static final int LIMIT = Integer.MAX_VALUE / 10;

    private IntParser() {
    }

    static int parse(DataSlice slice) {
        char[] data = slice.data();
        int from = slice.from();
        int to = slice.to();
        if (from >= to) {
            throw new NumberFormatException("Empty or whitespace-only input");
        }

        int result = 0;
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
                    throw new NumberFormatException("Integer overflow");
                }
                result = result * 10 + digit;
            } else {
                throw new NumberFormatException("Unexpected character for a numeric value: '" + c + "' in " + new String(data, from, to - from));
            }
        }
        return minus ? -result : result;
    }
}
