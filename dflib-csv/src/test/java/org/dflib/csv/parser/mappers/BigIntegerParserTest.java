package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BigIntegerParserTest {

    @Test
    void parsePositive() {
        BigInteger result = parse("12345");
        assertEquals(new BigInteger("12345"), result);
    }

    @Test
    void parseNegative() {
        BigInteger result = parse("-6789");
        assertEquals(new BigInteger("-6789"), result);
    }

    @Test
    void parseZero() {
        BigInteger result = parse("0");
        assertEquals(BigInteger.ZERO, result);
    }

    @Test
    void parseNegativeZero() {
        BigInteger result = parse("-0");
        assertEquals(BigInteger.ZERO, result);
    }

    @Test
    void parseInvalidCharacter() {
        assertThrows(NumberFormatException.class, () -> parse("12a34"));
    }

    @Test
    void parseEmptyString() {
        assertThrows(NumberFormatException.class, () -> parse(""));
    }

    @Test
    void parseOnlyWhitespace() {
        assertThrows(NumberFormatException.class, () -> parse("   "));
    }

    @Test
    void parseLeadingPlusUnsupported() {
        String s = "+123";
        assertThrows(NumberFormatException.class, () -> parse(s));
    }

    @Test
    void parseMinusOnly() {
        String s = "-";
        assertThrows(NumberFormatException.class, () -> parse(s));
    }

    @Test
    void parseVeryLargeNumber() {
        String s = "922337203685477580792233720368547758079223372036854775807"; // > Long.MAX_VALUE, repeated pattern
        BigInteger result = parse(s);
        assertEquals(new BigInteger(s), result);
    }

    private static BigInteger parse(String s) {
        return BigIntegerParser.parse(DataSlice.of(s.toCharArray()));
    }
}
