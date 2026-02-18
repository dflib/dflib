package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void defaultCommentPrefix() {
        String csv = """
                # comment line
                id,name
                1,A
                # ignored
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .enableComments()
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void multiCharCommentPrefix() {
        String csv = """
                // comment line
                id,name
                1,A
                // ignored
                2,B
                /3,C
                """;

        CsvFormat format = CsvFormat.builder()
                .enableComments("//")
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(3)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B")
                .expectRow(2, "/3", "C");
    }

    @Test
    void commentDisabled() {
        String csv = """
                #id,name
                1,A
                """;

        CsvFormat format = CsvFormat.builder()
                .build();

        new DfParserAsserts(csv, format, "#id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "A");
    }

    @Test
    void commentsInFile() {
        String csv = """
                # This is a comment line before header
                id,name,value
                # This is a comment line that should be ignored
                1,Name 1,Value 1
                # Another comment between data
                2,Name 2,Value 2
                3,Name 3,Value 3
                """;

        new DfParserAsserts(csv,
                CsvFormat.builder().enableComments(),
                "id", "name", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3");
    }

    @Test
    void commentsWithExplicitColumns() {
        String csv = """
                # header comment
                id,name
                1,A
                # ignored
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .column(CsvFormat.column("id"))
                .column(CsvFormat.column("name"))
                .enableComments("#")
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }
}
