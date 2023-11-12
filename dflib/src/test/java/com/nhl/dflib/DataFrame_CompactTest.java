package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_CompactTest {

    @Test
    public void compactInt_ByLabel() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(
                        "1", "x",
                        "5", "z",
                        "2", "y")
                .compactInt("a", IntValueMapper.fromString());


        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 5, "z")
                .expectRow(2, 2, "y");
    }

    @Test
    public void compactInt_ByPos() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(
                        "1", "x",
                        "5", "z",
                        "2", "y")
                .compactInt(0, -1);


        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 5, "z")
                .expectRow(2, 2, "y");
    }

    @Test
    public void compactInt_FromBoolean() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(
                        true, "x",
                        false, "z",
                        true, "y")
                .compactInt("a", IntValueMapper.fromObject());

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 0, "z")
                .expectRow(2, 1, "y");
    }


    @Test
    public void compactInt_Nulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "1", "x",
                null, "z",
                "2", "y");

        assertThrows(IllegalArgumentException.class, () -> df.compactInt(0, IntValueMapper.fromString()));
    }

    @Test
    public void compactInt_NullsDefault() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(
                        "1", "x",
                        null, "z",
                        "2", "y")
                .compactInt(0, -100);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, -100, "z")
                .expectRow(2, 2, "y");
    }
}

