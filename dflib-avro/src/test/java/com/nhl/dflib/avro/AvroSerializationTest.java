package com.nhl.dflib.avro;

import com.nhl.dflib.*;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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
                .expectRow(0, 1, 11)
                .expectRow(1, 2, 12)
                .expectRow(2, 3, null);
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
                .expectRow(0, Long.MAX_VALUE, 21L)
                .expectRow(1, Long.MIN_VALUE + 1L, 22L)
                .expectRow(2, 5L, null);
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
                .expectRow(0, 20.12, 30.1)
                .expectRow(1, 20.123, 31.45)
                .expectRow(2, 20.1235, null);
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
                .expectRow(0, true, true)
                .expectRow(1, false, false)
                .expectRow(2, true, null);
    }

    @Test
    public void testString() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.forData("s1", "s2", null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(3)
                .expectRow(0, "s1")
                .expectRow(1, "s2")
                .expectRow(2, (Object) null);
    }

    @Test
    public void testByteArray() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.forData(new byte[]{1, 2, 3}, new byte[0], null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(3)
                .expectRow(0, new byte[]{1, 2, 3})
                .expectRow(1, new byte[0])
                .expectRow(2, (Object) null);
    }

    @Test
    public void testDateTime() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.forData(LocalDate.of(2020, 1, 5), LocalDate.of(2019, 6, 8), null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2020, 1, 5))
                .expectRow(1, LocalDate.of(2019, 6, 8))
                .expectRow(2, (Object) null);
    }
}
