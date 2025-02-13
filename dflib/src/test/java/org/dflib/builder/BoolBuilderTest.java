package org.dflib.builder;

import org.dflib.BooleanSeries;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BoolBuilderTest {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 20, 64, 65, 128, 129, 200, 1000})
    void fillTrue(int len) {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> true, len);

        assertEquals(len, booleans.size());
        for(int i = 0; i < len; i++) {
            assertTrue(booleans.get(0), i + " element of " + len);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 4, 20, 64, 65, 128, 129, 200, 1000})
    void fillFalse(int len) {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> false, len);

        assertEquals(len, booleans.size());
        for(int i = 0; i < len; i++) {
            assertFalse(booleans.get(0), i + " element of " + len);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4, 20, 64, 65, 128, 129, 200, 1000})
    void fillMixed(int len) {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> i / 2 == 0, len);
        assertEquals(len, booleans.size());

        for (int i = 0; i < len; i++) {
            assertEquals(i / 2 == 0, booleans.get(i), i + " element of " + len);
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 4, 20, 64, 65, 128, 129, 200, 1000})
    void fillMixedInverted(int len) {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> i / 2 != 0, len);
        assertEquals(len, booleans.size());

        for (int i = 0; i < len; i++) {
            assertEquals(i / 2 != 0, booleans.get(i), i + " element of " + len);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "1, 10",
            "1, 64",
            "1, 65",
            "1, 128",
            "1, 129",
            "1, 200",

            "10, 10",
            "10, 64",
            "10, 65",
            "10, 128",
            "10, 129",
            "10, 200",

            "64, 64",
            "64, 65",
            "64, 128",
            "64, 129",
            "64, 200",

            "65, 65",
            "65, 128",
            "65, 129",
            "65, 200",

            "128, 128",
            "128, 129",
            "128, 200",

            "129, 129",
            "129, 200",

            "200, 200",
            "200, 1000",
    })
    void fillNTrueFirst(int countTrue, int len) {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> i < countTrue, len);

        assertEquals(len, booleans.size());
        for(int i = 0; i < countTrue; i++) {
            assertTrue(booleans.get(i), i + " element of " + len);
        }
        for(int i = countTrue; i < len; i++) {
            assertFalse(booleans.get(i), i + " element of " + len);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "1, 1",
            "1, 10",
            "1, 64",
            "1, 65",
            "1, 128",
            "1, 129",
            "1, 200",

            "10, 10",
            "10, 64",
            "10, 65",
            "10, 128",
            "10, 129",
            "10, 200",

            "64, 64",
            "64, 65",
            "64, 128",
            "64, 129",
            "64, 200",

            "65, 65",
            "65, 128",
            "65, 129",
            "65, 200",

            "128, 128",
            "128, 129",
            "128, 200",

            "129, 129",
            "129, 200",

            "200, 200",
            "200, 1000",
    })
    void fillNFalseFirst(int countFalse, int len) {
        BooleanSeries booleans = BoolBuilder.buildSeries(i -> i >= countFalse, len);

        assertEquals(len, booleans.size());
        for(int i = 0; i < countFalse; i++) {
            assertFalse(booleans.get(i), i + " element of " + len);
        }
        for(int i = countFalse; i < len; i++) {
            assertTrue(booleans.get(i), i + " element of " + len);
        }
    }
}