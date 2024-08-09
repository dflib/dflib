package org.dflib.parquet;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static java.nio.file.Files.createTempFile;
import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneOffset.ofHours;

/**
 * Test Parquet deserialization. Given that saver has been tested, we can use it
 * to generate content to read and test
 *
 */
public class ParquetLoaderTest {

    @Test
    @DisplayName("Integer Column")
    public void integerColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(1, 2, 3);

        Path file = createTempFile("integerColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }

    @Test
    @DisplayName("Int Column")
    public void intColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").ofInts(0, 18, 49, 32);
        Path file = createTempFile("intColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, 18)
                .expectRow(1, 49)
                .expectRow(2, 32);

    }

    @Test
    @DisplayName("Double Object Column")
    public void doubleObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(1.0, 2.0, 3.0);

        Path file = createTempFile("doubleObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, 1.0)
                .expectRow(1, 2.0)
                .expectRow(2, 3.0);
    }

    @Test
    @DisplayName("Double Column")
    public void doubleColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").ofDoubles(0.0, 18.0, 49.0, 32.0);
        Path file = createTempFile("doubleColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, 18.0)
                .expectRow(1, 49.0)
                .expectRow(2, 32.0);
    }

    @Test
    @DisplayName("Long Object Column")
    public void longObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(1L, 2L, 3L);

        Path file = createTempFile("longObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, 1L)
                .expectRow(1, 2L)
                .expectRow(2, 3L);
    }

    @Test
    @DisplayName("Long Column")
    public void longColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").ofLongs(0L, 18L, 49L, 32L);
        Path file = createTempFile("longColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, 18L)
                .expectRow(1, 49L)
                .expectRow(2, 32L);
    }

    @Test
    @DisplayName("Boolean Object Column")
    public void booleanObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(true, false, true);

        Path file = createTempFile("booleanObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, true)
                .expectRow(1, false)
                .expectRow(2, true);
    }

    @Test
    @DisplayName("Short Object Column")
    public void shortObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of((short) 1, (short) 2, (short) 3);

        Path file = createTempFile("shortObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, (short) 1)
                .expectRow(1, (short) 2)
                .expectRow(2, (short) 3);
    }

    @Test
    @DisplayName("Byte Object Column")
    public void byteObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of((byte) 1, (byte) 2, (byte) 3);

        Path file = createTempFile("byteObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, (byte) 1)
                .expectRow(1, (byte) 2)
                .expectRow(2, (byte) 3);
    }

    @Test
    @DisplayName("Float Object Column")
    public void floatObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(1.0f, 2.0f, 3.0f);

        Path file = createTempFile("floatObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, 1.0f)
                .expectRow(1, 2.0f)
                .expectRow(2, 3.0f);
    }

    @Test
    @DisplayName("String Column")
    public void stringColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of("one", "two", "three");

        Path file = createTempFile("stringColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, "one")
                .expectRow(1, "two")
                .expectRow(2, "three");
    }

    @Test
    @DisplayName("UUID Column")
    public void uuidColumn() throws IOException {
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        DataFrame df = DataFrame.foldByRow("a").of(uuid1, uuid2, uuid3);

        Path file = createTempFile("uuidColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, uuid1)
                .expectRow(1, uuid2)
                .expectRow(2, uuid3);
    }

    public enum EnumValues {
        one, two, three;
    }

    @Test
    @DisplayName("Enum Column")
    public void enumColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(EnumValues.one, EnumValues.two, EnumValues.three);

        Path file = createTempFile("enumColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        // Because Enum class type is not present in the file, and parquet reader can
        // not have it declared, enums are deserialized to String
        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, EnumValues.one.toString())
                .expectRow(1, EnumValues.two.toString())
                .expectRow(2, EnumValues.three.toString());
    }

    @Nested
    public class BigDecimalColumn {

        @Test
        @DisplayName("BigDecimal High Precision Column")
        public void highPrecision() throws IOException {
            var bigDec1 = new BigDecimal("12345678901234.56789");
            var bigDec2 = new BigDecimal("98765432109876.54321");
            var bigDec3 = new BigDecimal("12389137372521.35839");

            DataFrame df = DataFrame.foldByRow("a").of(bigDec1, bigDec2, bigDec3);

            Path file = createTempFile("bigDecimalColumn", ".parquet");
            Parquet.saver().bigDecimal(20, 5).save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, bigDec1)
                    .expectRow(1, bigDec2)
                    .expectRow(2, bigDec3);
        }

        @Test
        @DisplayName("BigDecimal Medium Precision Column")
        public void mediumPrecision() throws IOException {
            var bigDec1 = new BigDecimal("1234567890123.456");
            var bigDec2 = new BigDecimal("9876543210987.654");
            var bigDec3 = new BigDecimal("1238913737252.135");

            DataFrame df = DataFrame.foldByRow("a").of(bigDec1, bigDec2, bigDec3);

            Path file = createTempFile("bigDecimalColumn", ".parquet");
            Parquet.saver().bigDecimal(18, 3).save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, bigDec1)
                    .expectRow(1, bigDec2)
                    .expectRow(2, bigDec3);

        }

        @Test
        @DisplayName("BigDecimal Low Precision Column")
        public void lowPrecision() throws IOException {
            var bigDec1 = new BigDecimal("12345.6789");
            var bigDec2 = new BigDecimal("98765.4321");
            var bigDec3 = new BigDecimal("12389.1373");

            DataFrame df = DataFrame.foldByRow("a").of(bigDec1, bigDec2, bigDec3);

            Path file = createTempFile("bigDecimalColumn", ".parquet");
            Parquet.saver().bigDecimal(9, 4).save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, bigDec1)
                    .expectRow(1, bigDec2)
                    .expectRow(2, bigDec3);
        }

    }

    @Test
    @DisplayName("LocalDate Column")
    public void localDateColumn() throws IOException {
        LocalDate localDate1 = LocalDate.of(1969, 7, 20);
        LocalDate localDate2 = LocalDate.of(2001, 9, 11);
        LocalDate localDate3 = LocalDate.of(2004, 3, 11);

        DataFrame df = DataFrame.foldByRow("a").of(localDate1, localDate2, localDate3);

        Path file = createTempFile("localDateColumn", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a")
                .expectHeight(3)
                .expectRow(0, localDate1)
                .expectRow(1, localDate2)
                .expectRow(2, localDate3);
    }

    @Nested
    public class LocalTimeColumn {

        private final LocalTime localTime1 = LocalTime.of(10, 1, 18);
        private final LocalTime localTime2 = LocalTime.of(13, 9, 22);
        private final LocalTime localTime3 = LocalTime.of(20, 54, 00);

        @Test
        @DisplayName("LocalTime Column Micros")
        public void localTimeColumnMicros() throws IOException {

            DataFrame df = DataFrame.foldByRow("a").of(localTime1, localTime2, localTime3);

            Path file = createTempFile("localTimeColumnMicros", ".parquet");
            Parquet.saver().save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, localTime1)
                    .expectRow(1, localTime2)
                    .expectRow(2, localTime3);
        }

        @Test
        @DisplayName("LocalTime Column Millis")
        public void localTimeColumnMillis() throws IOException {
            DataFrame df = DataFrame.foldByRow("a").of(localTime1, localTime2, localTime3);

            Path file = createTempFile("localTimeColumnMillis", ".parquet");
            Parquet.saver().timeUnit(TimeUnit.MILLIS)
                    .save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, localTime1)
                    .expectRow(1, localTime2)
                    .expectRow(2, localTime3);
        }
    }

    @Nested
    public class LocalDateTimeColumn {

        private final LocalDateTime localDateTime1 = LocalDateTime.of(1969, 7, 20, 10, 1, 18);
        private final LocalDateTime localDateTime2 = LocalDateTime.of(2001, 9, 11, 8, 50, 22);
        private final LocalDateTime localDateTime3 = LocalDateTime.of(2004, 3, 11, 9, 10, 3);

        @Test
        @DisplayName("LocalDateTime Column Micros")
        public void localDateTimeColumnMicros() throws IOException {

            DataFrame df = DataFrame.foldByRow("a").of(localDateTime1, localDateTime2, localDateTime3);

            Path file = createTempFile("localDateTimeColumnMicros", ".parquet");
            Parquet.saver().save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, localDateTime1)
                    .expectRow(1, localDateTime2)
                    .expectRow(2, localDateTime3);
        }

        @Test
        @DisplayName("LocalDateTime Column Millis")
        public void localDateTimeColumnMillis() throws IOException {
            DataFrame df = DataFrame.foldByRow("a").of(localDateTime1, localDateTime2, localDateTime3);

            Path file = createTempFile("localDateTimeColumnMillis", ".parquet");
            Parquet.saver().timeUnit(TimeUnit.MILLIS)
                    .save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, localDateTime1)
                    .expectRow(1, localDateTime2)
                    .expectRow(2, localDateTime3);
        }
    }

    @Nested
    public class InstantColumn {

        private final LocalDateTime localDateTime1 = LocalDateTime.of(1969, 7, 20, 10, 1, 18);
        private final LocalDateTime localDateTime2 = LocalDateTime.of(2001, 9, 11, 8, 50, 22);
        private final LocalDateTime localDateTime3 = LocalDateTime.of(2004, 3, 11, 9, 10, 3);
        private final Instant instant1 = ofEpochSecond(localDateTime1.toEpochSecond(ofHours(-1)), 987654321);
        private final Instant instant2 = ofEpochSecond(localDateTime2.toEpochSecond(ofHours(-2)), 123456789);
        private final Instant instant3 = ofEpochSecond(localDateTime3.toEpochSecond(ofHours(-3)), 456789012);

        @Test
        @DisplayName("Instant Column Micros")
        public void instantColumnMicros() throws IOException {

            DataFrame df = DataFrame.foldByRow("a").of(instant1, instant2, instant3);

            Path file = createTempFile("instantColumnMicros", ".parquet");
            Parquet.saver().save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, ofEpochSecond(localDateTime1.toEpochSecond(ofHours(-1)), 987654000))
                    .expectRow(1, ofEpochSecond(localDateTime2.toEpochSecond(ofHours(-2)), 123456000))
                    .expectRow(2, ofEpochSecond(localDateTime3.toEpochSecond(ofHours(-3)), 456789000));

        }

        @Test
        @DisplayName("Instant Column Millis")
        public void instantColumnMillis() throws IOException {
            DataFrame df = DataFrame.foldByRow("a").of(instant1, instant2, instant3);

            Path file = createTempFile("instantColumnMillis", ".parquet");
            Parquet.saver().timeUnit(TimeUnit.MILLIS)
                    .save(df, file);

            DataFrame dfRead = Parquet.loader().load(file);

            new DataFrameAsserts(dfRead, "a")
                    .expectHeight(3)
                    .expectRow(0, ofEpochSecond(localDateTime1.toEpochSecond(ofHours(-1)), 987000000))
                    .expectRow(1, ofEpochSecond(localDateTime2.toEpochSecond(ofHours(-2)), 123000000))
                    .expectRow(2, ofEpochSecond(localDateTime3.toEpochSecond(ofHours(-3)), 456000000));
        }
    }

    @Test
    @DisplayName("Multiple Column Types")
    public void multipleColumnTypes() throws IOException {
        DataFrame df = DataFrame.byArrayRow("a", "b", "c", "d", "e", "f")
                .appender()
                .append(1, 2L, 3.0, 4.0f, true, "foo")
                .append(11, 12L, 13.0, 14.0f, false, "bar")
                .toDataFrame();

        Path file = createTempFile("multipleColumnTypes", ".parquet");
        Parquet.saver().save(df, file);

        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a", "b", "c", "d", "e", "f")
                .expectHeight(2)
                .expectRow(0, 1, 2L, 3.0, 4.0f, true, "foo")
                .expectRow(1, 11, 12L, 13.0, 14.0f, false, "bar");
    }

    @Test
    @DisplayName("Null Values")
    public void nullValues() throws IOException {
        DataFrame df = DataFrame.byArrayRow("a", "b", "c", "d", "e", "f")
                .appender()
                .append(1, 2L, 3.0, 4.0f, true, "foo")
                .append(null, null, null, null, null, null)
                .toDataFrame();

        Path file = createTempFile("nullValues", ".parquet");
        Parquet.saver().save(df, file);
        DataFrame dfRead = Parquet.loader().load(file);

        new DataFrameAsserts(dfRead, "a", "b", "c", "d", "e", "f")
                .expectHeight(2)
                .expectRow(0, 1, 2L, 3.0, 4.0f, true, "foo")
                .expectRow(1, null, null, null, null, null, null);
    }

}
