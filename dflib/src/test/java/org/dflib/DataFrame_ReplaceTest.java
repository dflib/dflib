package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.dflib.Exp.*;

@Deprecated
public class DataFrame_ReplaceTest {

    @Test
    public void convertColumn_Exp() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .replaceColumn("a", $int("a").mul(10));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void convertColumn_Exp_Position() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .replaceColumn("a", $int(0).mul(10));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void convertColumn_Exp_ToDate() {
        DataFrame df = DataFrame
                .foldByRow("a")
                .of(
                        "2018-01-05",
                        "2019-02-28",
                        null)
                .replaceColumn("a", $str("a").castAsDate());

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2018, 1, 5))
                .expectRow(1, LocalDate.of(2019, 2, 28))
                .expectRow(2, (Object) null);
    }

    @Test
    public void convertColumn_ValueMapperToDate_Formatter() {
        DataFrame df = DataFrame
                .foldByRow("a")
                .of(
                        "2018 01 05",
                        "2019 02 28",
                        null)
                .replaceColumn("a", $str("a").castAsDate(DateTimeFormatter.ofPattern("yyyy MM dd")));

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2018, 1, 5))
                .expectRow(1, LocalDate.of(2019, 2, 28))
                .expectRow(2, (Object) null);
    }

    // TODO: move the rest of the deprecated ValueMapper-based tests to DataFrame_Convert_ValueMapper_Test once we implement the expression analogs

    @Test
    public void convertColumn_ValueMapperToDateTime() {
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
}

