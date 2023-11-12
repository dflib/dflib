package com.nhl.dflib.avro;

import com.nhl.dflib.*;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;

public class AvroSerializationTest extends BaseAvroSerializationTest {

    @Test
    public void ints() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.ofInt(1, 2, 3),
                Series.of(11, 12, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectIntColumns(0)
                .expectColumn("c1", 1, 2, 3)
                .expectColumn("c2", 11, 12, null);
    }

    @Test
    public void longs() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.ofLong(Long.MAX_VALUE, Long.MIN_VALUE + 1L, 5L),
                Series.of(21L, 22L, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectLongColumns(0)
                .expectColumn("c1", Long.MAX_VALUE, Long.MIN_VALUE + 1L, 5L)
                .expectColumn("c2", 21L, 22L, null);
    }

    @Test
    public void doubles() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.ofDouble(20.12, 20.123, 20.1235),
                Series.of(30.1, 31.45, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectDoubleColumns(0)
                .expectColumn("c1", 20.12, 20.123, 20.1235)
                .expectColumn("c2", 30.1, 31.45, null);
    }

    @Test
    public void booleans() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.ofBool(true, false, true),
                Series.of(Boolean.TRUE, Boolean.FALSE, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectBooleanColumns(0)
                .expectColumn("c1", true, false, true)
                .expectColumn("c2", Boolean.TRUE, Boolean.FALSE, null);
    }

    @Test
    public void strings() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.of("s1", "s2", null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(3)
                .expectColumn("c1", "s1", "s2", null);
    }

    @Test
    public void byteArrays() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.of(new byte[]{1, 2, 3}, new byte[0], null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(3)
                .expectColumn("c1", new byte[]{1, 2, 3}, new byte[0], null);
    }

    @Test
    public void dateTime() {
        DataFrame df = DataFrame.newFrame("LocalDate", "LocalTime", "LocalDateTime", "YearMonth", "Year").columns(
                Series.of(LocalDate.of(2020, 1, 5), LocalDate.of(2019, 6, 8), null),
                Series.of(LocalTime.of(4, 0, 1, 11), LocalTime.of(23, 59, 59), null),
                Series.of(LocalDateTime.of(2200, 11, 5, 1, 2, 15, 9), LocalDateTime.of(1776, 6, 8, 6, 7, 8), null),
                Series.of(YearMonth.of(1651, 2), YearMonth.of(5000, 3), null),
                Series.of(Year.of(-45), Year.of(6000), null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "LocalDate", "LocalTime", "LocalDateTime", "YearMonth", "Year")
                .expectHeight(3)
                .expectColumn("LocalDate", LocalDate.of(2020, 1, 5), LocalDate.of(2019, 6, 8), null)
                .expectColumn("LocalTime", LocalTime.of(4, 0, 1, 11), LocalTime.of(23, 59, 59), null)
                .expectColumn("LocalDateTime", LocalDateTime.of(2200, 11, 5, 1, 2, 15, 9), LocalDateTime.of(1776, 6, 8, 6, 7, 8), null)
                .expectColumn("YearMonth", YearMonth.of(1651, 2), YearMonth.of(5000, 3), null)
                .expectColumn("Year", Year.of(-45), Year.of(6000), null);
    }

    @Test
    public void bigInteger() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.of(new BigInteger("987654321"), new BigInteger("-11111"), null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(3)
                .expectColumn("c1", new BigInteger("987654321"), new BigInteger("-11111"), null);
    }

    @Test
    public void bigDecimal() {
        DataFrame df = DataFrame.newFrame("c1").columns(
                Series.of(
                        new BigDecimal("9876543.211234567890123455"),
                        new BigDecimal(new BigInteger("-11111"), 2),
                        new BigDecimal(0),
                        null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1")
                .expectHeight(4)
                .expectColumn("c1",
                        new BigDecimal("9876543.211234567890123455"),
                        new BigDecimal("-111.11"),
                        new BigDecimal(0),
                        null);
    }

    @Test
    public void durations() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.of(Duration.ofDays(350000), Duration.ofSeconds(15, 101), Duration.ZERO, null),
                Series.of(Period.ofWeeks(15), Period.ofYears(5), Period.ZERO, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(4)
                .expectColumn("c1", Duration.ofDays(350000), Duration.ofSeconds(15, 101), Duration.ZERO, null)
                .expectColumn("c2", Period.ofWeeks(15), Period.ofYears(5), Period.ZERO, null);
    }

    @Test
    public void enums() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.of(TestEnum1.ab, TestEnum1.m, null),
                Series.of(TestEnum2.ab, TestEnum2.x, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectColumn("c1", TestEnum1.ab, TestEnum1.m, null)
                .expectColumn("c2", TestEnum2.ab, TestEnum2.x, null);
    }

    @Test
    public void unmapped() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.of(new TestUnmapped1("x"), new TestUnmapped1("y"), null),
                Series.of(new TestUnmapped2("a"), new TestUnmapped2("b"), null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(3)
                .expectColumn("c1", "x", "y", null)
                .expectColumn("c2", "a", "b", null);
    }

    @Test
    public void nulls() {
        DataFrame df = DataFrame.newFrame("c1", "c2").columns(
                Series.of(null, null),
                Series.of(null, null)
        );

        DataFrame loaded = saveAndLoad(df);
        new DataFrameAsserts(loaded, "c1", "c2")
                .expectHeight(2)
                .expectColumn("c1", null, null)
                .expectColumn("c2", null, null);
    }

    public enum TestEnum1 {
        m, z, ab
    }

    public enum TestEnum2 {
        a, x, ab
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
