package org.dflib.csv.parser.mappers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FloatingPointParserTest {

    @Test
    void matchInfinityInfLowercase() {
        char[] data = "inf".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 3));
    }

    @Test
    void matchInfinityInfUppercase() {
        char[] data = "INF".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 3));
    }

    @Test
    void matchInfinityInfMixedCase() {
        char[] data = "InF".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 3));
    }

    @Test
    void matchInfinityInfinityLowercase() {
        char[] data = "infinity".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 8));
    }

    @Test
    void matchInfinityInfinityUppercase() {
        char[] data = "INFINITY".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 8));
    }

    @Test
    void matchInfinityInfinityMixedCase() {
        char[] data = "InFiNiTy".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 8));
    }

    @Test
    void matchInfinityNegativeInf() {
        char[] data = "-Inf".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 4));
    }

    @Test
    void matchInfinityPositiveInf() {
        char[] data = "+Inf".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 4));
    }

    @Test
    void matchInfinityPositiveInfinity() {
        char[] data = "+Infinity".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 9));
    }

    @Test
    void matchInfinityNegativeInfinity() {
        char[] data = "-Infinity".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 0, 9));
    }

    @Test
    void matchInfinitySubstringInf() {
        char[] data = "xxxInfyyy".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 3, 6));
    }

    @Test
    void matchInfinitySubstringInfinity() {
        char[] data = "xxxInfinityyyy".toCharArray();
        assertTrue(FloatingPointParser.matchInfinity(data, 3, 11));
    }

    @Test
    void matchInfinityEmpty() {
        char[] data = "".toCharArray();
        assertFalse(FloatingPointParser.matchInfinity(data, 0, 0));
    }

    @Test
    void matchInfinityInvalidLengths() {
        assertFalse(FloatingPointParser.matchInfinity("i".toCharArray(), 0, 1));
        assertFalse(FloatingPointParser.matchInfinity("in".toCharArray(), 0, 2));
        assertFalse(FloatingPointParser.matchInfinity("infi".toCharArray(), 0, 4));
        assertFalse(FloatingPointParser.matchInfinity("infin".toCharArray(), 0, 5));
        assertFalse(FloatingPointParser.matchInfinity("infini".toCharArray(), 0, 6));
        assertFalse(FloatingPointParser.matchInfinity("infinit".toCharArray(), 0, 7));
        assertFalse(FloatingPointParser.matchInfinity("infinityx".toCharArray(), 0, 9));
    }

    @Test
    void matchInfinityInvalidValues() {
        assertFalse(FloatingPointParser.matchInfinity("nan".toCharArray(), 0, 3));
        assertFalse(FloatingPointParser.matchInfinity("inx".toCharArray(), 0, 3));
        assertFalse(FloatingPointParser.matchInfinity("infinita".toCharArray(), 0, 8));
    }

    @Test
    void matchNaNLowercase() {
        char[] data = "nan".toCharArray();
        assertTrue(FloatingPointParser.matchNaN(data, 0, 3));
    }

    @Test
    void matchNaNUppercase() {
        char[] data = "NAN".toCharArray();
        assertTrue(FloatingPointParser.matchNaN(data, 0, 3));
    }

    @Test
    void matchNaNMixedCase() {
        char[] data = "NaN".toCharArray();
        assertTrue(FloatingPointParser.matchNaN(data, 0, 3));
    }

    @Test
    void matchNaNPositiveSign() {
        char[] data = "+NaN".toCharArray();
        assertTrue(FloatingPointParser.matchNaN(data, 0, 4));
    }

    @Test
    void matchNaNNegativeSign() {
        char[] data = "-nAn".toCharArray();
        assertTrue(FloatingPointParser.matchNaN(data, 0, 4));
    }

    @Test
    void matchNaNSubstring() {
        char[] data = "xxxNaNyyy".toCharArray();
        assertTrue(FloatingPointParser.matchNaN(data, 3, 6));
    }

    @Test
    void matchNaNEmpty() {
        char[] data = "".toCharArray();
        assertFalse(FloatingPointParser.matchNaN(data, 0, 0));
    }

    @Test
    void matchNaNInvalidLengths() {
        assertFalse(FloatingPointParser.matchNaN("n".toCharArray(), 0, 1));
        assertFalse(FloatingPointParser.matchNaN("na".toCharArray(), 0, 2));
        assertFalse(FloatingPointParser.matchNaN("nanx".toCharArray(), 0, 4));
        assertFalse(FloatingPointParser.matchNaN("+".toCharArray(), 0, 1));
    }

    @Test
    void matchNaNInvalidValues() {
        assertFalse(FloatingPointParser.matchNaN("inf".toCharArray(), 0, 3));
        assertFalse(FloatingPointParser.matchNaN("nax".toCharArray(), 0, 3));
        assertFalse(FloatingPointParser.matchNaN("n(a)n".toCharArray(), 0, 5));
    }
}
