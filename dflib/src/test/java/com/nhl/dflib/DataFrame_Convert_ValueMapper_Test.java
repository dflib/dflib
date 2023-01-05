package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataFrame_Convert_ValueMapper_Test {

    @Test
    public void testConvertColumn() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .convertColumn("a", v -> ((int) v) * 10);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testConvertColumn_ByPos() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .convertColumn(0, v -> ((int) v) * 10);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testConvertColumn_WithNulls() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, null)
                .convertColumn(1, v -> v != null ? "not null" : "null");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "not null")
                .expectRow(1, 2, "null");
    }

    @Test
    public void testConvertColumn_ValueMapperToDate() {
        DataFrame df = DataFrame
                .foldByRow("a")
                .of(
                        "2018-01-05",
                        "2019-02-28",
                        null)
                .convertColumn("a", ValueMapper.stringToDate());

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2018, 1, 5))
                .expectRow(1, LocalDate.of(2019, 2, 28))
                .expectRow(2, (Object) null);
    }

    @Test
    public void testConvertColumn_ValueMapperToDate_Formatter() {
        DataFrame df = DataFrame
                .foldByRow("a")
                .of(
                        "2018 01 05",
                        "2019 02 28",
                        null)
                .convertColumn("a", ValueMapper.stringToDate(DateTimeFormatter.ofPattern("yyyy MM dd")));

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2018, 1, 5))
                .expectRow(1, LocalDate.of(2019, 2, 28))
                .expectRow(2, (Object) null);
    }
}

