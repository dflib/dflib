package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class CsvLoader_NullString extends BaseCsvTest {

    @Test
    public void nullString() {
        DataFrame df = new CsvLoader()
                .nullString(":")
                .load(inPath("strings_w_colons.csv"));

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, null, "three")
                .expectRow(1, "five", null);
    }

    @Test
    public void emptyStringIsNull() {
        DataFrame df = new CsvLoader()
                .emptyStringIsNull()
                .load(inPath("strings_w_nulls.csv"));

        new DataFrameAsserts(df, "One", "Two")
                .expectHeight(2)
                .expectRow(0, null, "three")
                .expectRow(1, "five", null);
    }
}
