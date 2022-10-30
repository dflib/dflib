package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.nhl.dflib.Exp.$int;
import static com.nhl.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.*;

public class DataFrame_ConvertTest {

    @Test
    public void testConvertColumn_Exp() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(1, "x", 2, "y")
                .convertColumn("a", $int("a").mul(10));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testConvertColumn_Exp_Position() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(1, "x", 2, "y")
                .convertColumn("a", $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testConvertColumn_Exp_ToDate() {
        DataFrame df = DataFrame
                .newFrame("a")
                .foldByRow(
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
                .newFrame("a")
                .foldByRow(
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
                .newFrame("a")
                .foldByRow(
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

    @Test
    public void testConvertColumnToInt_ByLabel() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        "1", "x",
                        "5", "z",
                        "2", "y")
                .toIntColumn("a", IntValueMapper.fromString());


        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 5, "z")
                .expectRow(2, 2, "y");
    }

    @Test
    public void testConvertColumnToInt_ByPos() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        "1", "x",
                        "5", "z",
                        "2", "y")
                .toIntColumn(0, -1);


        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 5, "z")
                .expectRow(2, 2, "y");
    }

    @Test
    public void testConvertColumnToInt_FromBoolean() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        true, "x",
                        false, "z",
                        true, "y")
                .toIntColumn("a", IntValueMapper.fromObject());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 0, "z")
                .expectRow(2, 1, "y");
    }


    @Test
    public void testConvertColumnToInt_Nulls() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "1", "x",
                null, "z",
                "2", "y");

        assertThrows(IllegalArgumentException.class, () -> df.toIntColumn(0, IntValueMapper.fromString()));
    }

    @Test
    public void testConvertColumnToInt_NullsDefault() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        "1", "x",
                        null, "z",
                        "2", "y")
                .toIntColumn(0, -100);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, -100, "z")
                .expectRow(2, 2, "y");
    }

    @Test
    public void testToEnumFromNumColumn() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
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

    @Test
    public void testToEnumFromStringColumn() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
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

