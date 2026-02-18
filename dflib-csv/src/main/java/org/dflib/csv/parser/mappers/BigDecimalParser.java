package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;

import java.math.BigDecimal;
import java.math.BigInteger;

final class BigDecimalParser {

    private static final int BLOCK_SIZE = 9;
    private static final BigInteger BIG_TEN_POW_BLOCK = BigInteger.valueOf(1_000_000_000L);

    private BigDecimalParser() {
    }

    static BigDecimal parse(DataSlice slice) {
        char[] data = slice.data();
        int from = slice.from();
        int to = slice.to();

        if (from >= to) {
            throw new NumberFormatException("Empty input");
        }

        boolean minus = false;
        if (data[from] == '-') {
            minus = true;
            from++;
        }

        if (from >= to) {
            throw new NumberFormatException("No digits found");
        }

        BigInteger unscaled = BigInteger.ZERO;

        long block = 0;
        int blockLength = 0;
        int scale = 0;
        boolean seenDot = false;

        for (int i = from; i < to; i++) {
            char c = data[i];

            if (c >= '0' && c <= '9') {
                block = block * 10 + (c - '0');
                blockLength++;

                if (seenDot) {
                    scale++;
                }

                if (blockLength == BLOCK_SIZE) {
                    unscaled = unscaled
                            .multiply(BIG_TEN_POW_BLOCK)
                            .add(BigInteger.valueOf(block));
                    block = 0;
                    blockLength = 0;
                }

            } else if (c == '.' && !seenDot) {
                seenDot = true;

            } else {
                throw new NumberFormatException("Invalid character: '" + c + "'");
            }
        }

        if (blockLength > 0) {
            unscaled = unscaled.multiply(BigInteger.TEN.pow(blockLength))
                    .add(BigInteger.valueOf(block));
        }

        if (minus) {
            unscaled = unscaled.negate();
        }

        return new BigDecimal(unscaled, scale);
    }
}
