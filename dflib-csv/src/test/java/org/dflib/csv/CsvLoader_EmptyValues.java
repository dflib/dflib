package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvLoader_EmptyValues {

    static final ByteSource EMPTY_WHOLES = ByteSource.of("""
            One,Two
            ,3
            5,""".getBytes());

    static final ByteSource EMPTY_DECIMALS = ByteSource.of("""
            One,Two
            ,3.1
            5.2002,""".getBytes());

    static final ByteSource EMPTY_STRINGS = ByteSource.of("""
            One,Two
            "",three
            five,""".getBytes());

    @Test
    public void emptyString() {
        DataFrame df = new CsvLoader().load(EMPTY_STRINGS);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, "", "three")
                .expectRow(1, "five", "");
    }

    @Test
    public void nullString() {
        String csv = """
                One,Two
                ":",three
                five,:""";

        DataFrame df = new CsvLoader()
                .nullString(":")
                .load(ByteSource.of(csv.getBytes()));

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, null, "three")
                .expectRow(1, "five", null);
    }

    @Test
    public void emptyStringIsNull() {
        DataFrame df = new CsvLoader()
                .emptyStringIsNull()
                .load(EMPTY_STRINGS);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, null, "three")
                .expectRow(1, "five", null);
    }

    @Test
    public void emptyInt_Throws() {
        CsvLoader loader = new CsvLoader()
                .intCol(0)
                .intCol(1);

        assertThrows(IllegalArgumentException.class, () -> loader.load(EMPTY_WHOLES));
    }

    @Test
    public void emptyInt_Default() {
        DataFrame df = new CsvLoader()
                .intCol(0, -100)
                .intCol(1, -200)
                .load(EMPTY_WHOLES);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectIntColumns(0, 1)
                .expectRow(0, -100, 3)
                .expectRow(1, 5, -200);
    }

    @Test
    public void emptyLong_Throws() {
        CsvLoader loader = new CsvLoader()
                .longCol(0)
                .longCol(1);

        assertThrows(IllegalArgumentException.class, () -> loader.load(EMPTY_WHOLES));
    }

    @Test
    public void emptyLong_Default() {
        DataFrame df = new CsvLoader()
                .longCol(0, -100L)
                .longCol(1, -200L)
                .load(EMPTY_WHOLES);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectLongColumns(0, 1)
                .expectRow(0, -100L, 3L)
                .expectRow(1, 5L, -200L);
    }

    @Test
    public void doubleCol_Nulls_Default() {
        DataFrame df = new CsvLoader()
                .doubleCol(0, -1.1)
                .doubleCol(1, -3.14)
                .load(EMPTY_DECIMALS);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1.1, 3.1)
                .expectRow(1, 5.2002, -3.14);
    }

    @Test
    public void decimalCol() {
        DataFrame df = new CsvLoader()
                .decimalCol(0)
                .decimalCol(1)
                .load(EMPTY_DECIMALS);

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, null, new BigDecimal("3.1"))
                .expectRow(1, new BigDecimal("5.2002"), null);
    }

}
