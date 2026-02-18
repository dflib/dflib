package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class LimitOffsetTest {

    private static final String CSV = """
            id,name,value
            1,A,foo
            2,B,bar
            3,C,baz
            4,D,qux
            """;

    @Test
    void excludeHeaderOffset0Limit2() {
        CsvFormat.Builder format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .offset(0)
                .limit(2);

        new DfParserAsserts(CSV, format, "id", "name", "value")
                .expectHeight(2)
                .expectRow(0, "1", "A", "foo")
                .expectRow(1, "2", "B", "bar");
    }

    @Test
    void excludeHeaderOffset1Limit2() {
        CsvFormat.Builder format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .offset(1)
                .limit(2);

        new DfParserAsserts(CSV, format, "1", "A", "foo")
                .expectHeight(2)
                .expectRow(0, "2", "B", "bar")
                .expectRow(1, "3", "C", "baz");
    }

    @Test
    void includeHeaderOffset0Limit2() {
        CsvFormat.Builder format = CsvFormat.builder()
                .autoColumns(true)
                .excludeHeaderValues(false)
                .offset(0)
                .limit(2);

        new DfParserAsserts(CSV, format, "c0", "c1", "c2")
                .expectHeight(2)
                .expectRow(0, "id", "name", "value")
                .expectRow(1, "1", "A", "foo");
    }

    @Test
    void limitAndOffset() {
        CsvFormat format = CsvFormat.builder()
                .offset(1)
                .limit(2)
                .build();

        new DfParserAsserts(CSV, format, "1", "A", "foo")
                .expectHeight(2)
                .expectColumn("1", "2", "3");
    }
}
