package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DoubleParserTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "12345",
            "-6789",
            "123.456",
            ".5",
            "42.",
            "1e3",
            "1E3",
            "1e-3",
            "1e+3",
            "-1e3",
            "9223372036854775808.5",
            "-9223372036854775809.5",
            "0.1234567890123456789",
            "1.7976931348623157E308",
            "1.7976931348623159E308",
            "4.9e-324",
            "1e-325",
            "-0",
            "-0.0"
    })
    void parseFiniteAndBoundaryValuesMatchJdk(String s) {
        double expected = Double.parseDouble(s);
        double actual = parse(s);
        assertEquals(Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(actual));
    }

    @Test
    void parseLeadingPlusUnsupported() {
        assertThrows(NumberFormatException.class, () -> parse("+123.0"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Inf",
            "Infinity",
            "InFiNiTy",
            "+Inf",
            "+Infinity"
    })
    void parsePositiveInfinityForms(String s) {
        assertEquals(Double.POSITIVE_INFINITY, parse(s));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "-Inf",
            "-Infinity"
    })
    void parseNegativeInfinityForms(String s) {
        assertEquals(Double.NEGATIVE_INFINITY, parse(s));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "nan",
            "NaN",
            "+nAn",
            "-NAN"
    })
    void parseNaNForms(String s) {
        assertTrue(Double.isNaN(parse(s)));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "+",
            "-",
            ".",
            "-.",
            "+.",
            "12a34",
            "1..2",
            "1e",
            "1e+",
            "1e-",
            "nanx",
            "infinityx"
    })
    void parseInvalidValues(String s) {
        assertThrows(NumberFormatException.class, () -> parse(s));
    }

    @Test
    void parseSliceFinite() {
        char[] data = "xxx123.456yyy".toCharArray();
        double expected = Double.parseDouble("123.456");
        double actual = DoubleParser.parse(slice(data, 3, 10));
        assertEquals(Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(actual));
    }

    @Test
    void parseSliceExponentFallback() {
        char[] data = "xxx1.25e3yyy".toCharArray();
        double expected = Double.parseDouble("1.25e3");
        double actual = DoubleParser.parse(slice(data, 3, 9));
        assertEquals(Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(actual));
    }

    private static double parse(String s) {
        return DoubleParser.parse(DataSlice.of(s.toCharArray()));
    }

    private static DataSlice slice(char[] data, int from, int to) {
        DataSlice slice = new DataSlice();
        slice.setData(data);
        slice.setFrom(from);
        slice.setTo(to);
        return slice;
    }
}
