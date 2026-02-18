package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BooleanParserTest {

    @Test
    void parseTrue() {
        boolean result = parse("true");
        assertTrue(result);
    }

    @Test
    void parseFalse() {
        boolean result = parse("false");
        assertFalse(result);
    }

    @Test
    void parseInvalidValue() {
        boolean result = parse("notabool");
        assertFalse(result);
    }

    @Test
    void parseOne() {
        boolean result = parse("1");
        assertTrue(result);
    }

    @Test
    void parseZero() {
        boolean result = parse("0");
        assertFalse(result);
    }

    @Test
    void parseEmptyString() {
        boolean result = parse("");
        assertFalse(result);
    }

    private static boolean parse(String s) {
        return BoolParser.parse(DataSlice.of(s.toCharArray()));
    }

}
