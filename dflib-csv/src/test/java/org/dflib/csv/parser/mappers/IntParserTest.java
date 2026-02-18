package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IntParserTest {

    @Test
    void parsePositive() {
        long result = parse("12345");
        assertEquals(12345, result);
    }

    @Test
    void parseNegative() {
        long result = parse("-6789");
        assertEquals(-6789, result);
    }

    @Test
    void parseInvalidCharacter() {
        assertThrows(NumberFormatException.class, () -> parse("12a34"));
    }

    @Test
    void parseOnlyWhitespace() {
        assertThrows(NumberFormatException.class, () -> parse("   "));
    }

    // Limits
    @Test
    void parseMaxValue() {
        String s = Long.toString(Integer.MAX_VALUE);
        long result = parse(s);
        assertEquals(Integer.MAX_VALUE, result);
    }

    @Test
    void parseMinValue() {
        String s = Long.toString(Integer.MIN_VALUE);
        long result = parse(s);
        assertEquals(Integer.MIN_VALUE, result);
    }

    @Test
    void parseOverflowPositive() {
        String s = "2147483648"; // Integer.MAX_VALUE + 1
        assertThrows(NumberFormatException.class, () -> parse(s));
    }

    @Test
    void parseUnderflowNegative() {
        String s = "-2147483649"; // Integer.MIN_VALUE - 1
        assertThrows(NumberFormatException.class, () -> parse(s));
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

    private static int parse(String s) {
        return IntParser.parse(DataSlice.of(s.toCharArray()));
    }

}
