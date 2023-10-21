package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.nhl.dflib.Exp.$int;
import static com.nhl.dflib.Exp.$str;

public class DataFrame_ConvertTest {

    @Test
    public void testConvertColumn_Exp() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .convertColumn("a", $int("a").mul(10));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testConvertColumn_Exp_Position() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .convertColumn("a", $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testConvertColumn_Exp_ToDate() {
        DataFrame df = DataFrame
                .foldByRow("a")
                .of(
                        "2018-01-05",
                        "2019-02-28",
                        null)
                .convertColumn("a", $str("a").castAsDate());

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
                .convertColumn("a", $str("a").castAsDate(DateTimeFormatter.ofPattern("yyyy MM dd")));

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2018, 1, 5))
                .expectRow(1, LocalDate.of(2019, 2, 28))
                .expectRow(2, (Object) null);
    }

    // TODO: move the rest of the deprecated ValueMapper-based tests to DataFrame_Convert_ValueMapper_Test once we implement the expression analogs

    @Test
    public void testConvertColumn_ValueMapperToDateTime() {
        DataFrame df = DataFrame
                .foldByRow("a")
                .of(
                        "2018-01-05T00:01:15",
                        "2019-02-28T13:11:12",
                        null)
                .convertColumn("a", ValueMapper.stringToDateTime());

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDateTime.of(2018, 1, 5, 0, 1, 15))
                .expectRow(1, LocalDateTime.of(2019, 2, 28, 13, 11, 12))
                .expectRow(2, (Object) null);
    }

    @Deprecated
    @Test
    public void testToEnumFromNumColumn() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(
                        1, "x",
                        null, "z",
                        0, "y")
                .toEnumFromNumColumn(0, X.class);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, X.b, "x")
                .expectRow(1, null, "z")
                .expectRow(2, X.a, "y");
    }

    @Deprecated
    @Test
    public void testToEnumFromStringColumn() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(
                        "b", "x",
                        null, "z",
                        "a", "y")
                .toEnumFromStringColumn(0, X.class);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, X.b, "x")
                .expectRow(1, null, "z")
                .expectRow(2, X.a, "y");
    }

    enum X {a, b, c, d, e}
}

