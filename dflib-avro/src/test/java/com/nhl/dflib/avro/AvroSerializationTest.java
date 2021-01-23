package com.nhl.dflib.avro;

import com.nhl.dflib.*;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
        DataFrame df = DataFrame.newFrame("LocalDate", "LocalTime", "LocalDateTime").columns(
                Series.forData(LocalDate.of(2020, 1, 5), LocalDate.of(2019, 6, 8), null),
                Series.forData(LocalTime.of(4, 0, 1, 11), LocalTime.of(23, 59, 59), null),
                Series.forData(LocalDateTime.of(2200, 11, 5, 1, 2, 15, 9), LocalDateTime.of(1776, 6, 8, 6, 7, 8), null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "LocalDate", "LocalTime", "LocalDateTime")
                .expectHeight(3)
                .expectColumn("LocalDate", LocalDate.of(2020, 1, 5), LocalDate.of(2019, 6, 8), null)
                .expectColumn("LocalTime", LocalTime.of(4, 0, 1, 11), LocalTime.of(23, 59, 59), null)
                .expectColumn("LocalDateTime", LocalDateTime.of(2200, 11, 5, 1, 2, 15, 9), LocalDateTime.of(1776, 6, 8, 6, 7, 8), null);
    }

    @Test
    public void testUnmapped() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.forData(new TestUnmapped1("x"), new TestUnmapped1("y"), null),
                Series.forData(new TestUnmapped2("a"), new TestUnmapped2("b"), null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectColumn("c1", "x", "y", null)
                .expectColumn("c2", "a", "b", null);
    }

    static final class TestUnmapped1 {

        private final String value;

        public TestUnmapped1(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    static final class TestUnmapped2 {

        private final String value;

        public TestUnmapped2(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
}
