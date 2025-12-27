package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class LoadLogicalTypesTest {

    @TempDir
    static Path outBase;

    // per https://avro.apache.org/docs/1.12.0/specification/#logical-types

    @Test
    public void uuid() {
        record R(UUID uuid) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"uuid","type":{"type":"string","logicalType":"uuid"}}
                          ]
                        }""")
                .writer((r, o) -> r.put("uuid", o.uuid()))
                .write(
                        new R(UUID.nameUUIDFromBytes(new byte[]{6, 60, 120})),
                        new R(UUID.nameUUIDFromBytes(new byte[]{7, 70, 127})));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df,
                "uuid")
                .expectHeight(2)
                .expectRow(0, UUID.nameUUIDFromBytes(new byte[]{6, 60, 120}))
                .expectRow(1, UUID.nameUUIDFromBytes(new byte[]{7, 70, 127}));
    }

    @Test
    public void date() {
        record R(LocalDate ld) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"ld","type":{"type":"int","logicalType":"date"}}
                          ]
                        }""")
                .writer((r, o) -> r.put("ld", o.ld()))
                .write(
                        new R(LocalDate.of(2025, 12, 1)),
                        new R(LocalDate.of(2025, 11, 3)));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "ld")
                .expectHeight(2)
                .expectRow(0, LocalDate.of(2025, 12, 1))
                .expectRow(1, LocalDate.of(2025, 11, 3));
    }

    @Test
    public void time() {
        record R(LocalTime ltMicro, LocalTime ltMilli) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"ltMicro","type":{"type":"long","logicalType":"time-micros"}},
                            {"name":"ltMilli","type":{"type":"int","logicalType":"time-millis"}}
                          ]
                        }""")
                .writer((r, o) -> {
                    r.put("ltMicro", o.ltMicro());
                    r.put("ltMilli", o.ltMilli());
                })
                .write(
                        new R(
                                LocalTime.of(4, 5, 6, 66_000),
                                LocalTime.of(4, 5, 6, 66_000_000)),
                        new R(
                                LocalTime.of(5, 8, 6, 55_000),
                                LocalTime.of(5, 8, 6, 55_000_000)));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df,
                "ltMicro", "ltMilli")
                .expectHeight(2)
                .expectRow(0,
                        LocalTime.of(4, 5, 6, 66_000),
                        LocalTime.of(4, 5, 6, 66_000_000))

                .expectRow(1,
                        LocalTime.of(5, 8, 6, 55_000),
                        LocalTime.of(5, 8, 6, 55_000_000));
    }

    @Test
    public void timestampInstant() {
        record R(Instant instantMilli, Instant instantMicro, Instant instantNano) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"instantMilli","type":{"type":"long","logicalType":"timestamp-millis"}},
                            {"name":"instantMicro","type":{"type":"long","logicalType":"timestamp-micros"}},
                            {"name":"instantNano","type":{"type":"long","logicalType":"timestamp-nanos"}}
                          ]
                        }""")
                .writer((r, o) -> {
                    r.put("instantMilli", o.instantMilli());
                    r.put("instantMicro", o.instantMicro());
                    r.put("instantNano", o.instantNano());
                })
                .write(
                        new R(
                                LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000_000).toInstant(ZoneOffset.UTC),
                                LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000).toInstant(ZoneOffset.UTC),
                                LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66).toInstant(ZoneOffset.UTC)),

                        new R(
                                LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000_000).toInstant(ZoneOffset.UTC),
                                LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000).toInstant(ZoneOffset.UTC),
                                LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55).toInstant(ZoneOffset.UTC)));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "instantMilli", "instantMicro", "instantNano")
                .expectHeight(2)
                .expectRow(0,
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66).toInstant(ZoneOffset.UTC))
                .expectRow(1,
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000).toInstant(ZoneOffset.UTC),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55).toInstant(ZoneOffset.UTC));
    }

    @Test
    public void timestampLocal() {
        record R(LocalDateTime ldtMilli, LocalDateTime ldtMicro, LocalDateTime ldtNano) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"ldtMilli","type":{"type":"long","logicalType":"local-timestamp-millis"}},
                            {"name":"ldtMicro","type":{"type":"long","logicalType":"local-timestamp-micros"}},
                            {"name":"ldtNano","type":{"type":"long","logicalType":"local-timestamp-nanos"}}
                          ]
                        }""")
                .writer((r, o) -> {
                    r.put("ldtMilli", o.ldtMilli());
                    r.put("ldtMicro", o.ldtMicro());
                    r.put("ldtNano", o.ldtNano());
                })
                .write(
                        new R(LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000_000),
                                LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000),
                                LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66)
                        ),

                        new R(LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000_000),
                                LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000),
                                LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55)
                        ));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df, "ldtMilli", "ldtMicro", "ldtNano")
                .expectHeight(2)
                .expectRow(0,
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000_000),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66_000),
                        LocalDateTime.of(2025, 11, 5, 2, 3, 4, 66))
                .expectRow(1,
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000_000),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55_000),
                        LocalDateTime.of(2024, 12, 5, 2, 3, 4, 55));
    }

    @Test
    public void decimal() {
        record R(BigDecimal decimal, BigDecimal bigDecimal) {
        }

        Path p = TestWriter.of(R.class, outBase)
                .schema("""
                        {
                          "type":"record",
                          "name":"DataFrame",
                          "fields":[
                            {"name":"decimal","type":{"type":"bytes", "logicalType":"decimal","precision": 18, "scale": 4}},
                            {"name":"bigDecimal","type":{"type" : "bytes", "logicalType":"big-decimal"}}
                          ]
                        }""")
                .writer((r, o) -> {
                    r.put("decimal", o.decimal());
                    r.put("bigDecimal", o.bigDecimal());
                })
                .write(
                        new R(new BigDecimal("3567.0001"),
                                new BigDecimal("23456.102")),

                        new R(new BigDecimal("5567.0002"),
                                new BigDecimal("-33456.201")));

        DataFrame df = Avro.load(p);

        new DataFrameAsserts(df,
                "decimal", "bigDecimal")
                .expectHeight(2)
                .expectRow(0,
                        new BigDecimal("3567.0001"),
                        new BigDecimal("23456.102"))

                .expectRow(1,
                        new BigDecimal("5567.0002"),
                        new BigDecimal("-33456.201"));
    }
}
