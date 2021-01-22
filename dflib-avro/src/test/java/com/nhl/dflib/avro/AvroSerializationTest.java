package com.nhl.dflib.avro;

import com.nhl.dflib.*;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AvroSerializationTest extends BaseAvroSerializationTest {

    @Test
    public void testInt() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                IntSeries.forInts(1, 2, 3),
                Series.forData(11, 12, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectIntColumns(0)
                .expectColumn("c1", 1, 2, 3)
                .expectColumn("c2", 11, 12, null);
    }

    @Test
    public void testLong() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                LongSeries.forLongs(Long.MAX_VALUE, Long.MIN_VALUE + 1L, 5L),
                Series.forData(21L, 22L, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectLongColumns(0)
                .expectColumn("c1", Long.MAX_VALUE, Long.MIN_VALUE + 1L, 5L)
                .expectColumn("c2", 21L, 22L, null);
    }

    @Test
    public void testDouble() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                DoubleSeries.forDoubles(20.12, 20.123, 20.1235),
                Series.forData(30.1, 31.45, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectDoubleColumns(0)
                .expectColumn("c1", 20.12, 20.123, 20.1235)
                .expectColumn("c2", 30.1, 31.45, null);
    }

    @Test
    public void testBoolean() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                BooleanSeries.forBooleans(true, false, true),
                Series.forData(Boolean.TRUE, Boolean.FALSE, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectBooleanColumns(0)
                .expectColumn("c1", true, false, true)
                .expectColumn("c2", Boolean.TRUE, Boolean.FALSE, null);
    }

    @Test
    public void testString() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.forData("s1", "s2", null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(3)
                .expectColumn("c1", "s1", "s2", null);
    }

    @Test
    public void testByteArray() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.forData(new byte[]{1, 2, 3}, new byte[0], null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(3)
                .expectColumn("c1", new byte[]{1, 2, 3}, new byte[0], null);
    }

    @Test
    public void testDateTime() {
        DataFrame df = DataFrame.newFrame("LocalDate", "LocalDateTime").columns(
                Series.forData(LocalDate.of(2020, 1, 5), LocalDate.of(2019, 6, 8), null),
                Series.forData(LocalDateTime.of(2200, 11, 5, 1, 2, 15, 9), LocalDateTime.of(1776, 6, 8, 6, 7, 8), null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "LocalDate", "LocalDateTime")
                .expectHeight(3)
                .expectColumn("LocalDate", LocalDate.of(2020, 1, 5), LocalDate.of(2019, 6, 8), null)
                .expectColumn("LocalDateTime", LocalDateTime.of(2200, 11, 5, 1, 2, 15, 9), LocalDateTime.of(1776, 6, 8, 6, 7, 8), null);
    }
}
