package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BigDecimalParserTest {

    @Test
    void parsePositiveInteger() {
        BigDecimal result = parse("12345");
        assertEquals(new BigDecimal("12345"), result);
    }

    @Test
    void parseNegativeInteger() {
        BigDecimal result = parse("-6789");
        assertEquals(new BigDecimal("-6789"), result);
    }

    @Test
    void parseFractional() {
        BigDecimal result = parse("123.456");
        assertEquals(new BigDecimal("123.456"), result);
    }

    @Test
    void parseLeadingDotFraction() {
        BigDecimal result = parse(".5");
        assertEquals(new BigDecimal("0.5"), result);
    }

    @Test
    void parseTrailingDot() {
        BigDecimal result = parse("42.");
        assertEquals(BigDecimal.valueOf(42), result);
    }

    @Test
    void parseNegativeZero() {
        BigDecimal result = parse("-0");
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void parseInvalidCharacter() {
        assertThrows(NumberFormatException.class, () -> parse("12a34"));
    }

    @Test
    void parseMultipleDotsUnsupported() {
        assertThrows(NumberFormatException.class, () -> parse("1.2.3"));
    }

    @Test
    void parseLeadingPlusUnsupported() {
        assertThrows(NumberFormatException.class, () -> parse("+123.0"));
    }

    @Test
    void parseExponentUnsupported() {
        assertThrows(NumberFormatException.class, () -> parse("1e3"));
    }

    @Test
    void parseOnlyWhitespace() {
        assertThrows(NumberFormatException.class, () -> parse("   "));
    }

    @Test
    void parseEmptyString() {
        assertThrows(NumberFormatException.class, () -> parse(""));
    }

    @Test
    void parseVeryLargeDecimal() {
        String s = "123456789012345678901234567890.12345678901234567890";
        BigDecimal result = parse(s);
        assertEquals(new BigDecimal(s), result);
    }

    private static BigDecimal parse(String s) {
        return BigDecimalParser.parse(DataSlice.of(s.toCharArray()));
    }
}
