package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;

import java.math.BigInteger;

final class BigIntegerParser {

    private BigIntegerParser() {
    }

    static BigInteger parse(DataSlice slice) {
        char[] data = slice.data();
        int from = slice.from();
        int to = slice.to();

        if (from >= to) {
            throw new NumberFormatException("Empty input");
        }

        boolean negative = false;
        if (data[from] == '-') {
            negative = true;
            from++;
        }

        if (from >= to) {
            throw new NumberFormatException("No digits found");
        }

        BigInteger result = BigInteger.ZERO;

        for (int i = from; i < to; i++) {
            char c = data[i];
            if (c < '0' || c > '9') {
                throw new NumberFormatException("Invalid character: '" + c + "'");
            }
            int digit = c - '0';
            result = result.multiply(BigInteger.TEN).add(BigInteger.valueOf(digit));
        }

        return negative ? result.negate() : result;
    }
}
