package org.dflib.parquet;

import org.apache.avro.Conversions;
import org.apache.avro.data.TimeConversions;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.conf.PlainParquetConfiguration;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.io.LocalInputFile;
import org.dflib.DataFrame;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test Parquet serialization. To verify generated file content we use existing
 * Parquet Avro reader.
 *
 */
public class ParquetSaverTest {

    @Test
    @DisplayName("Integer Column")
    public void integerColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(1, 2, 3);

        Path file = createTempFile("integerColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("optional int32 a;"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(1, reader.read().get("a"));
        assertEquals(2, reader.read().get("a"));
        assertEquals(3, reader.read().get("a"));
    }

    @Test
    @DisplayName("Int Column")
    public void intColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").ofInts(0, 18, 49, 32);
        Path file = createTempFile("intColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("required int32 a;"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(18, reader.read().get("a"));
        assertEquals(49, reader.read().get("a"));
        assertEquals(32, reader.read().get("a"));

    }

    @Test
    @DisplayName("Double Object Column")
    public void doubleObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(1.0, 2.0, 3.0);

        Path file = createTempFile("doubleObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("optional double a;"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(1.0, reader.read().get("a"));
        assertEquals(2.0, reader.read().get("a"));
        assertEquals(3.0, reader.read().get("a"));
    }

    @Test
    @DisplayName("Double Column")
    public void doubleColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").ofDoubles(0.0, 18.0, 49.0, 32.0);
        Path file = createTempFile("doubleColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("required double a;"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(18.0, reader.read().get("a"));
        assertEquals(49.0, reader.read().get("a"));
        assertEquals(32.0, reader.read().get("a"));
    }

    @Test
    @DisplayName("Long Object Column")
    public void longObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(1L, 2L, 3L);

        Path file = createTempFile("longObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("optional int64 a;"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(1L, reader.read().get("a"));
        assertEquals(2L, reader.read().get("a"));
        assertEquals(3L, reader.read().get("a"));
    }

    @Test
    @DisplayName("Long Column")
    public void longColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").ofLongs(0L, 18L, 49L, 32L);
        Path file = createTempFile("longColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("required int64 a;"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(18L, reader.read().get("a"));
        assertEquals(49L, reader.read().get("a"));
        assertEquals(32L, reader.read().get("a"));
    }

    @Test
    @DisplayName("Boolean Object Column")
    public void booleanObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(true, false, true);

        Path file = createTempFile("booleanObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("optional boolean a;"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(true, reader.read().get("a"));
        assertEquals(false, reader.read().get("a"));
        assertEquals(true, reader.read().get("a"));
    }

    @Test
    @DisplayName("Short Object Column")
    public void shortObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of((short) 1, (short) 2, (short) 3);

        Path file = createTempFile("shortObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("optional int32 a (INTEGER(16,true));"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(1, reader.read().get("a"));
        assertEquals(2, reader.read().get("a"));
        assertEquals(3, reader.read().get("a"));
    }

    @Test
    @DisplayName("Byte Object Column")
    public void byteObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of((byte) 1, (byte) 2, (byte) 3);

        Path file = createTempFile("byteObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("optional int32 a (INTEGER(8,true));"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(1, reader.read().get("a"));
        assertEquals(2, reader.read().get("a"));
        assertEquals(3, reader.read().get("a"));
    }

    @Test
    @DisplayName("Float Object Column")
    public void floatObjectColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of(1.0f, 2.0f, 3.0f);

        Path file = createTempFile("floatObjectColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("optional float a;"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(1.0f, reader.read().get("a"));
        assertEquals(2.0f, reader.read().get("a"));
        assertEquals(3.0f, reader.read().get("a"));
    }

    @Test
    @DisplayName("String Column")
    public void stringColumn() throws IOException {
        DataFrame df = DataFrame.foldByRow("a").of("one", "two", "three");

        Path file = createTempFile("stringColumn", ".parquet");
        Parquet.saver().save(df, file);

        String schema = getSchema(file);
        assertTrue(schema.contains("optional binary a (STRING);"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals("one", reader.read().get("a").toString());
        assertEquals("two", reader.read().get("a").toString());
        assertEquals("three", reader.read().get("a").toString());
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

        String schema = getSchema(file);
        assertTrue(schema.contains("optional fixed_len_byte_array(16) a (UUID);"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(uuid1.toString(), reader.read().get("a"));
        assertEquals(uuid2.toString(), reader.read().get("a"));
        assertEquals(uuid3.toString(), reader.read().get("a"));
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

        String schema = getSchema(file);
        assertTrue(schema.contains("optional binary a (ENUM);"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(EnumValues.one.toString(), reader.read().get("a").toString());
        assertEquals(EnumValues.two.toString(), reader.read().get("a").toString());
        assertEquals(EnumValues.three.toString(), reader.read().get("a").toString());
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

            String schema = getSchema(file);
            assertTrue(schema.contains("optional binary a (DECIMAL(20,5));"));

            ParquetReader<GenericRecord> reader = getAvroReader(file);
            assertEquals(bigDec1, reader.read().get("a"));
            assertEquals(bigDec2, reader.read().get("a"));
            assertEquals(bigDec3, reader.read().get("a"));
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

            String schema = getSchema(file);
            assertTrue(schema.contains("optional int64 a (DECIMAL(18,3));"));

            ParquetReader<GenericRecord> reader = getAvroReader(file);
            // Avro reader doesn't support int64 encoding. Validate through Long value
            assertEquals(1234567890123456L, reader.read().get("a"));
            assertEquals(9876543210987654L, reader.read().get("a"));
            assertEquals(1238913737252135L, reader.read().get("a"));
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

            String schema = getSchema(file);
            assertTrue(schema.contains("optional int32 a (DECIMAL(9,4));"));

            ParquetReader<GenericRecord> reader = getAvroReader(file);
            // Avro reader doesn't support int32 encoding. Validate through Int value
            assertEquals(123456789, reader.read().get("a"));
            assertEquals(987654321, reader.read().get("a"));
            assertEquals(123891373, reader.read().get("a"));
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

        String schema = getSchema(file);
        assertTrue(schema.contains("optional int32 a (DATE);"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        assertEquals(localDate1, reader.read().get("a"));
        assertEquals(localDate2, reader.read().get("a"));
        assertEquals(localDate3, reader.read().get("a"));
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

            String schema = getSchema(file);
            assertTrue(schema.contains("optional int64 a (TIME(MICROS,false));"));

            ParquetReader<GenericRecord> reader = getAvroReader(file);
            assertEquals(localTime1, reader.read().get("a"));
            assertEquals(localTime2, reader.read().get("a"));
            assertEquals(localTime3, reader.read().get("a"));
        }

        @Test
        @DisplayName("LocalTime Column Millis")
        public void localTimeColumnMillis() throws IOException {
            DataFrame df = DataFrame.foldByRow("a").of(localTime1, localTime2, localTime3);

            Path file = createTempFile("localTimeColumnMillis", ".parquet");
            Parquet.saver().timeUnit(TimeUnit.MILLIS)
                    .save(df, file);

            String schema = getSchema(file);
            assertTrue(schema.contains("optional int32 a (TIME(MILLIS,false));"));

            ParquetReader<GenericRecord> reader = getAvroReaderMillis(file);
            assertEquals(localTime1, reader.read().get("a"));
            assertEquals(localTime2, reader.read().get("a"));
            assertEquals(localTime3, reader.read().get("a"));
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

            String schema = getSchema(file);
            assertTrue(schema.contains("optional int64 a (TIMESTAMP(MICROS,false));"));

            ParquetReader<GenericRecord> reader = getAvroReader(file);
            assertEquals(localDateTime1, reader.read().get("a"));
            assertEquals(localDateTime2, reader.read().get("a"));
            assertEquals(localDateTime3, reader.read().get("a"));
        }

        @Test
        @DisplayName("LocalDateTime Column Millis")
        public void localDateTimeColumnMillis() throws IOException {
            DataFrame df = DataFrame.foldByRow("a").of(localDateTime1, localDateTime2, localDateTime3);

            Path file = createTempFile("localDateTimeColumnMillis", ".parquet");
            Parquet.saver().timeUnit(TimeUnit.MILLIS)
                    .save(df, file);

            String schema = getSchema(file);
            assertTrue(schema.contains("optional int64 a (TIMESTAMP(MILLIS,false));"));

            ParquetReader<GenericRecord> reader = getAvroReaderMillis(file);
            assertEquals(localDateTime1, reader.read().get("a"));
            assertEquals(localDateTime2, reader.read().get("a"));
            assertEquals(localDateTime3, reader.read().get("a"));
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

            String schema = getSchema(file);
            assertTrue(schema.contains("optional int64 a (TIMESTAMP(MICROS,true));"));

            ParquetReader<GenericRecord> reader = getAvroReader(file);
            assertEquals(ofEpochSecond(localDateTime1.toEpochSecond(ofHours(-1)), 987654000), reader.read().get("a"));
            assertEquals(ofEpochSecond(localDateTime2.toEpochSecond(ofHours(-2)), 123456000), reader.read().get("a"));
            assertEquals(ofEpochSecond(localDateTime3.toEpochSecond(ofHours(-3)), 456789000), reader.read().get("a"));
        }

        @Test
        @DisplayName("Instant Column Millis")
        public void instantColumnMillis() throws IOException {
            DataFrame df = DataFrame.foldByRow("a").of(instant1, instant2, instant3);

            Path file = createTempFile("instantColumnMillis", ".parquet");
            Parquet.saver().timeUnit(TimeUnit.MILLIS)
                    .save(df, file);

            String schema = getSchema(file);
            assertTrue(schema.contains("optional int64 a (TIMESTAMP(MILLIS,true));"));

            ParquetReader<GenericRecord> reader = getAvroReaderMillis(file);
            assertEquals(ofEpochSecond(localDateTime1.toEpochSecond(ofHours(-1)), 987000000), reader.read().get("a"));
            assertEquals(ofEpochSecond(localDateTime2.toEpochSecond(ofHours(-2)), 123000000), reader.read().get("a"));
            assertEquals(ofEpochSecond(localDateTime3.toEpochSecond(ofHours(-3)), 456000000), reader.read().get("a"));
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

        String schema = getSchema(file);
        assertTrue(schema.contains("optional int32 a;"));
        assertTrue(schema.contains("optional int64 b;"));
        assertTrue(schema.contains("optional double c;"));
        assertTrue(schema.contains("optional float d;"));
        assertTrue(schema.contains("optional boolean e;"));
        assertTrue(schema.contains("optional binary f (STRING);"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        GenericRecord row1 = reader.read();
        assertEquals(1, row1.get("a"));
        assertEquals(2L, row1.get("b"));
        assertEquals(3.0, row1.get("c"));
        assertEquals(4.0f, row1.get("d"));
        assertEquals(true, row1.get("e"));
        assertEquals("foo", row1.get("f").toString());

        GenericRecord row2 = reader.read();
        assertEquals(11, row2.get("a"));
        assertEquals(12L, row2.get("b"));
        assertEquals(13.0, row2.get("c"));
        assertEquals(14.0f, row2.get("d"));
        assertEquals(false, row2.get("e"));
        assertEquals("bar", row2.get("f").toString());
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

        String schema = getSchema(file);
        assertTrue(schema.contains("optional int32 a;"));
        assertTrue(schema.contains("optional int64 b;"));
        assertTrue(schema.contains("optional double c;"));
        assertTrue(schema.contains("optional float d;"));
        assertTrue(schema.contains("optional boolean e;"));
        assertTrue(schema.contains("optional binary f (STRING);"));

        ParquetReader<GenericRecord> reader = getAvroReader(file);
        GenericRecord row1 = reader.read();
        assertEquals(1, row1.get("a"));
        assertEquals(2L, row1.get("b"));
        assertEquals(3.0, row1.get("c"));
        assertEquals(4.0f, row1.get("d"));
        assertEquals(true, row1.get("e"));
        assertEquals("foo", row1.get("f").toString());

        GenericRecord row2 = reader.read();
        assertEquals(null, row2.get("a"));
        assertEquals(null, row2.get("b"));
        assertEquals(null, row2.get("c"));
        assertEquals(null, row2.get("d"));
        assertEquals(null, row2.get("e"));
        assertEquals(null, row2.get("f"));
    }

    private ParquetReader<GenericRecord> getAvroReader(Path file) throws IOException {
        GenericData genericData = GenericData.get();
        genericData.addLogicalTypeConversion(new Conversions.DecimalConversion());
        genericData.addLogicalTypeConversion(new TimeConversions.DateConversion());
        genericData.addLogicalTypeConversion(new TimeConversions.LocalTimestampMicrosConversion());
        genericData.addLogicalTypeConversion(new TimeConversions.TimeMicrosConversion());
        genericData.addLogicalTypeConversion(new TimeConversions.TimestampMicrosConversion());
        return AvroParquetReader
                .<GenericRecord>builder(new LocalInputFile(file), new PlainParquetConfiguration())
                .withDataModel(genericData)
                .build();
    }

    private ParquetReader<GenericRecord> getAvroReaderMillis(Path file) throws IOException {
        GenericData genericData = GenericData.get();
        genericData.addLogicalTypeConversion(new TimeConversions.DateConversion());
        genericData.addLogicalTypeConversion(new TimeConversions.LocalTimestampMillisConversion());
        genericData.addLogicalTypeConversion(new TimeConversions.TimeMillisConversion());
        genericData.addLogicalTypeConversion(new TimeConversions.TimestampMillisConversion());
        return AvroParquetReader
                .<GenericRecord>builder(new LocalInputFile(file), new PlainParquetConfiguration())
                .withDataModel(genericData)
                .build();
    }

    private String getSchema(Path file) throws IOException {
        try (ParquetFileReader reader = ParquetFileReader.open(new LocalInputFile(file))) {
            return reader.getFileMetaData().getSchema().toString();
        }
    }
}
