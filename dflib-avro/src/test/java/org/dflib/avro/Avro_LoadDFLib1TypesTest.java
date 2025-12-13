package org.dflib.avro;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;

public class Avro_LoadDFLib1TypesTest {

    @Test
    public void load() {
        DataFrame df = Avro.load(ByteSource.ofUrl(getClass().getResource("dflib1.avro")));

        new DataFrameAsserts(df, "bd", "bi", "bytes", "duration", "ld", "ldt", "lt", "period", "ym", "year", "custom")
                .expectHeight(2)
                .expectRow(0, new BigDecimal("3567.0001"), new BigInteger("23456"), new byte[]{50, 51, 52}, Duration.ofDays(1),
                        LocalDate.of(2025, 12, 1), LocalDateTime.of(2025, 11, 5, 2, 3, 4), LocalTime.of(4, 5, 6),
                        Period.ofDays(2), YearMonth.of(2026, 1), Year.of(2027), "AR[a=2, b=aaa]")
                .expectRow(1, new BigDecimal("5567.0002"), new BigInteger("33456"), new byte[] {60, 61, 62}, Duration.ofMinutes(5),
                        LocalDate.of(2025, 11, 3), LocalDateTime.of(2024, 12, 5, 2, 3, 4), LocalTime.of(5, 8, 6),
                        Period.ofDays(3), YearMonth.of(2026, 3), Year.of(2028), "AR[a=3, b=bbb]");
    }
}
