package org.dflib.avro;

import org.apache.avro.Conversions;
import org.apache.avro.Schema;
import org.apache.avro.data.TimeConversions;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public class LoadSpecLogicalTypesTest {

    @TempDir
    static Path outBase;

    // per https://avro.apache.org/docs/1.12.0/specification/#logical-types
    static final Schema SCHEMA = new Schema.Parser().parse("""
            {
              "type":"record",
              "name":"DataFrame",
              "namespace":"org.dflib",
              "fields":[
                {"name":"decimal","type":{"type":"bytes", "logicalType":"decimal","precision": 18, "scale": 4}},
                {"name":"bigDecimal","type":{"type" : "bytes", "logicalType":"big-decimal"}},
                {"name":"ld","type":{"type":"int","logicalType":"date"}},
                {"name":"instantMicro","type":{"type":"long","logicalType":"timestamp-micros"}},
                {"name":"instantMilli","type":{"type":"long","logicalType":"timestamp-millis"}},
                {"name":"ldtMicro","type":{"type":"long","logicalType":"local-timestamp-micros"}},
                {"name":"ldtMilli","type":{"type":"long","logicalType":"local-timestamp-millis"}},
                {"name":"ltMicro","type":{"type":"long","logicalType":"time-micros"}},
                {"name":"ltMilli","type":{"type":"int","logicalType":"time-millis"}},
                {"name":"uuid","type":{"type":"string","logicalType":"uuid"}}
              ]
            }""");

    public record R1(
            BigDecimal decimal,
            BigDecimal bigDecimal,
            LocalDate ld,
            Instant instantMicro,
            Instant instantMilli,
            LocalDateTime ldtMicro,
            LocalDateTime ldtMilli,
            LocalTime ltMicro,
            LocalTime ltMilli,
            UUID uuid) {
    }

    static Path createAvroFile() throws IOException {

        Path out = outBase.resolve("java.avro");
        List<R1> list = List.of(
                new R1(new BigDecimal("3567.0001"),
                        new BigDecimal("23456.102"),
                        LocalDate.of(2025, 12, 1),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000_000),
                        LocalTime.of(4, 5, 6, 66_000),
                        LocalTime.of(4, 5, 6, 66_000_000),
                        UUID.nameUUIDFromBytes(new byte[]{6, 60, 120})),

                new R1(new BigDecimal("5567.0002"),
                        new BigDecimal("-33456.201"),
                        LocalDate.of(2025, 11, 3),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000_000),
                        LocalTime.of(5, 8, 6, 55_000),
                        LocalTime.of(5, 8, 6, 55_000_000),
                        UUID.nameUUIDFromBytes(new byte[]{7, 70, 127}))
        );

        GenericData data = new GenericData();
        data.addLogicalTypeConversion(new Conversions.DecimalConversion());
        data.addLogicalTypeConversion(new Conversions.BigDecimalConversion());
        data.addLogicalTypeConversion(new TimeConversions.DateConversion());
        data.addLogicalTypeConversion(new TimeConversions.TimestampMicrosConversion());
        data.addLogicalTypeConversion(new TimeConversions.TimestampMillisConversion());
        data.addLogicalTypeConversion(new TimeConversions.LocalTimestampMicrosConversion());
        data.addLogicalTypeConversion(new TimeConversions.LocalTimestampMillisConversion());
        data.addLogicalTypeConversion(new TimeConversions.TimeMicrosConversion());
        data.addLogicalTypeConversion(new TimeConversions.TimeMillisConversion());
        data.addLogicalTypeConversion(new Conversions.UUIDConversion());

        var datumWriter = new GenericDatumWriter<GenericRecord>(SCHEMA, data);

        try (var fileWriter = new DataFileWriter<>(datumWriter)) {
            fileWriter.create(SCHEMA, out.toFile());

            for (R1 o : list) {
                GenericRecord r = new GenericData.Record(SCHEMA);
                r.put("decimal", o.decimal());
                r.put("bigDecimal", o.bigDecimal());
                r.put("ld", o.ld());
                r.put("instantMicro", o.instantMicro());
                r.put("instantMilli", o.instantMilli());
                r.put("ldtMicro", o.ldtMicro());
                r.put("ldtMilli", o.ldtMilli());
                r.put("ltMicro", o.ltMicro());
                r.put("ltMilli", o.ltMilli());
                r.put("uuid", o.uuid());

                fileWriter.append(r);
            }
        }

        return out;
    }

    @Test
    public void load() throws IOException {
        DataFrame df = Avro.load(createAvroFile());

        new DataFrameAsserts(df,
                "decimal", "bigDecimal", "ld", "instantMicro", "instantMilli", "ldtMicro", "ldtMilli", "ltMicro", "ltMilli", "uuid")
                .expectHeight(2)
                .expectRow(0,
                        new BigDecimal("3567.0001"),
                        new BigDecimal("23456.102"),
                        LocalDate.of(2025, 12, 1),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000_000),
                        LocalTime.of(4, 5, 6, 66_000),
                        LocalTime.of(4, 5, 6, 66_000_000),
                        UUID.nameUUIDFromBytes(new byte[]{6, 60, 120}))

                .expectRow(1,
                        new BigDecimal("5567.0002"),
                        new BigDecimal("-33456.201"),
                        LocalDate.of(2025, 11, 3),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000_000),
                        LocalTime.of(5, 8, 6, 55_000),
                        LocalTime.of(5, 8, 6, 55_000_000),
                        UUID.nameUUIDFromBytes(new byte[]{7, 70, 127}));
    }
}
