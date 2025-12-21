package org.dflib.avro;

import org.apache.avro.Conversions;
import org.apache.avro.data.TimeConversions;
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
import java.util.UUID;

public class Load_LogicalTypesTest {

    @TempDir
    static Path outBase;

    // per https://avro.apache.org/docs/1.12.0/specification/#logical-types
    static final String SCHEMA = """
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
            }""";

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

    static Path createAvroFile() {

        return TestWriter.of(R1.class, outBase)
                .schema(SCHEMA)
                .customizer(d -> {
                    d.addLogicalTypeConversion(new Conversions.DecimalConversion());
                    d.addLogicalTypeConversion(new Conversions.BigDecimalConversion());
                    d.addLogicalTypeConversion(new TimeConversions.DateConversion());
                    d.addLogicalTypeConversion(new TimeConversions.TimestampMicrosConversion());
                    d.addLogicalTypeConversion(new TimeConversions.TimestampMillisConversion());
                    d.addLogicalTypeConversion(new TimeConversions.LocalTimestampMicrosConversion());
                    d.addLogicalTypeConversion(new TimeConversions.LocalTimestampMillisConversion());
                    d.addLogicalTypeConversion(new TimeConversions.TimeMicrosConversion());
                    d.addLogicalTypeConversion(new TimeConversions.TimeMillisConversion());
                    d.addLogicalTypeConversion(new Conversions.UUIDConversion());
                })
                .writer((r, o) -> {
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
                })
                .write(
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
                                UUID.nameUUIDFromBytes(new byte[]{7, 70, 127})));
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
