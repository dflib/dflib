package org.dflib.csv.parser.mappers;

final class FloatingPointParser {

    private FloatingPointParser() {
    }

    static boolean matchNaN(char[] data, int from, int to) {
        if (from >= to) {
            return false;
        }

        if (data[from] == '+' || data[from] == '-') {
            from++;
        }

        return (to - from == 3)
                && (data[from] == 'N' || data[from] == 'n')
                && (data[from + 1] == 'A' || data[from + 1] == 'a')
                && (data[from + 2] == 'N' || data[from + 2] == 'n');
    }

    static boolean matchInfinity(char[] data, int from, int to) {
        if (from >= to) {
            return false;
        }

        if (data[from] == '+' || data[from] == '-') {
            from++;
        }

        int len = to - from;
        if (len == 3) {
            return (data[from] == 'I' || data[from] == 'i')
                    && (data[from + 1] == 'N' || data[from + 1] == 'n')
                    && (data[from + 2] == 'F' || data[from + 2] == 'f');
        }

        if (len == 8) {
            return (data[from] == 'I' || data[from] == 'i')
                    && (data[from + 1] == 'N' || data[from + 1] == 'n')
                    && (data[from + 2] == 'F' || data[from + 2] == 'f')
                    && (data[from + 3] == 'I' || data[from + 3] == 'i')
                    && (data[from + 4] == 'N' || data[from + 4] == 'n')
                    && (data[from + 5] == 'I' || data[from + 5] == 'i')
                    && (data[from + 6] == 'T' || data[from + 6] == 't')
                    && (data[from + 7] == 'Y' || data[from + 7] == 'y');
        }

        return false;
    }
}
