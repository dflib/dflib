package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class DelimiterTest {

    @ParameterizedTest
    @MethodSource("delimitersAndPaths")
    void multiCharDelimiter(String delimiter, String csv) {
        CsvFormat format = CsvFormat.defaultFormat().delimiter(delimiter).build();

        assertContent(csv, format);
    }

    @Test
    void customSingleCharDelimiter() {
        String semicolonCsv = """
                id;name;value
                1;A;foo
                2;B;bar
                3;C;baz
                """;

        CsvFormat semicolon = CsvFormat.defaultFormat().delimiter(";").build();

        new DfParserAsserts(semicolonCsv, semicolon, "id", "name", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3");

        CsvFormat pipe = CsvFormat.defaultFormat().delimiter("||").build();

        String pipeCsv = """
                id||name||value
                1||A||foo
                2||B||bar
                3||C||baz
                """;

        new DfParserAsserts(pipeCsv, pipe, "id", "name", "value")
                .expectHeight(3)
                .expectColumn("name", "A", "B", "C");
    }

    @Test
    void unquotedValueMultiDelimiter() {
        String csv = """
                id|||name|||value
                1|||A|||foo||bar
                2|||B|||baz
                """;

        CsvFormat format = CsvFormat.defaultFormat().delimiter("|||").build();

        new DfParserAsserts(csv, format, "id", "name", "value")
                .expectHeight(2)
                .expectRow(0, "1", "A", "foo||bar");
    }

    private void assertContent(String csv, CsvFormat format) {
        new DfParserAsserts(csv, format, "id", "name", "value")
                .expectHeight(4)
                .expectRow(0, "1", "Name 1", "Value 1")
                .expectRow(1, "2", "Name 2", "Value 2")
                .expectRow(2, "3", "Name 3", "Value 3")
                .expectRow(3, "4", "Name 4", "Value 4");
    }

    static Stream<Arguments> delimitersAndPaths() {
        return Stream.of(
                Arguments.of("|", """
                        id|name|value
                        1|Name 1|Value 1
                        2|Name 2|Value 2
                        3|Name 3|Value 3
                        4|Name 4|Value 4
                        """),
                Arguments.of("||", """
                        id||name||value
                        1||Name 1||Value 1
                        2||Name 2||Value 2
                        3||Name 3||Value 3
                        4||Name 4||Value 4
                        """),
                Arguments.of("|||", """
                        id|||name|||value
                        1|||Name 1|||Value 1
                        2|||Name 2|||Value 2
                        3|||Name 3|||Value 3
                        4|||Name 4|||Value 4
                        """)
        );
    }
}
