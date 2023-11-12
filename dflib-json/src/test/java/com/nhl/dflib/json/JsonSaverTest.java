package com.nhl.dflib.json;

import com.nhl.dflib.DataFrame;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonSaverTest {

    @Test
    @DisplayName("single column")
    public void singleColumn() {

        DataFrame df = DataFrame.foldByRow("a").of(1, 2, 3);

        StringWriter out = new StringWriter();
        Json.saver().save(df, out);
        assertEquals("[{\"a\":1},{\"a\":2},{\"a\":3}]", out.toString());
    }

    @Test
    @DisplayName("multi-column, different types")
    public void multiColumn_Types() {

        LocalDate date = LocalDate.of(2021, 2, 1);
        LocalDateTime timestamp = LocalDateTime.of(2021, 2, 1, 1, 2, 3);

        DataFrame df = DataFrame.foldByRow("a", "b", "c", "d", "e").of(
                1, "B", date, timestamp, true,
                2, "C", date.plusDays(1), timestamp, false,
                3, "D", date.minusDays(1), timestamp, true);

        StringWriter out = new StringWriter();
        Json.saver().save(df, out);
        assertEquals("[{\"a\":1,\"b\":\"B\",\"c\":\"2021-02-01\",\"d\":\"2021-02-01T01:02:03\",\"e\":true}," +
                "{\"a\":2,\"b\":\"C\",\"c\":\"2021-02-02\",\"d\":\"2021-02-01T01:02:03\",\"e\":false}," +
                "{\"a\":3,\"b\":\"D\",\"c\":\"2021-01-31\",\"d\":\"2021-02-01T01:02:03\",\"e\":true}]", out.toString());
    }
}
