package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("UnnecessaryStringEscape")
class LineBreakTest {

    @Test
    void multilineFields() {
        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("id").type(CsvColumnType.INTEGER))
                .column(CsvColumnMapping.column("name"))
                .column(CsvColumnMapping.column("value"))
                .build();

        String csv = """
                id,name,value
                1,"Name 1 with
                newline",Value 1
                2,Name 2,"Multi
                Line
                Value"
                3,"Name 3","Value 3"
                """;

        new DfParserAsserts(csv, format, "id", "name", "value")
                .expectHeight(3)
                .expectRow(0, 1, "Name 1 with\nnewline", "Value 1")
                .expectRow(1, 2, "Name 2", "Multi\nLine\nValue");
    }

    @Test
    void linebreakLf() {
        String csv = """
                id,value
                1,Value 1
                2,Value 2
                3,Value 3""";

        CsvFormat format = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();

        new DfParserAsserts(csv, format, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "Value 1")
                .expectRow(1, "2", "Value 2")
                .expectRow(2, "3", "Value 3");
    }

    @Test
    void linebreakCr() {
        String csv = "id,value\r1,Value 1\r2,Value 2\r3,Value 3";

        CsvFormat format = CsvFormat.defaultFormat().lineBreak(LineBreak.CR).build();

        new DfParserAsserts(csv, format, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "Value 1")
                .expectRow(1, "2", "Value 2")
                .expectRow(2, "3", "Value 3");
    }

    @Test
    void linebreakCrlf() {
        String csv = """
                id,value\r
                1,Value 1\r
                2,Value 2\r
                3,Value 3""";

        CsvFormat format = CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF).build();

        new DfParserAsserts(csv, format, "id", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3")
                .expectColumn("value", "Value 1", "Value 2", "Value 3");
    }

    @Test
    void trailingEmptyRow() {
        String csv = """
                id,name,value
                1,Name 1,Value 1
                2,Name 2,Value 2
                3,Name 3,Value 3
                """;

        new DfParserAsserts(csv, "id", "name", "value")
                .expectHeight(3)
                .expectRow(2, "3", "Name 3", "Value 3");
    }

    @Test
    void crTrailingEmptyRow() {
        String csv = "id,value\r1,Value 1\r2,Value 2\r3,Value 3\r";

        CsvFormat format = CsvFormat.defaultFormat().lineBreak(LineBreak.CR).build();

        new DfParserAsserts(csv, format, "id", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3")
                .expectColumn("value", "Value 1", "Value 2", "Value 3");
    }

    @Test
    void crlfTrailingEmptyRow() {
        String csv = """
                id,value\r
                1,Value 1\r
                2,Value 2\r
                3,Value 3\r
                """;

        CsvFormat format = CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF).build();

        new DfParserAsserts(csv, format, "id", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3")
                .expectColumn("value", "Value 1", "Value 2", "Value 3");
    }

    @Test
    void autoDetectLf() {
        String csv = """
                id,value
                1,Value 1
                2,Value 2
                3,Value 3""";

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "Value 1")
                .expectRow(1, "2", "Value 2")
                .expectRow(2, "3", "Value 3");
    }

    @Test
    void autoDetectCr() {
        String csv = "id,value\r1,Value 1\r2,Value 2\r3,Value 3";

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "Value 1")
                .expectRow(1, "2", "Value 2")
                .expectRow(2, "3", "Value 3");
    }

    @Test
    void autoDetectCrlf() {
        String csv = """
                id,value\r
                1,Value 1\r
                2,Value 2\r
                3,Value 3""";

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3")
                .expectColumn("value", "Value 1", "Value 2", "Value 3");
    }

    @Test
    void autoMixed() {
        String csv = """
                id,value
                1,Value 1\r2,Value 2\r
                3,Value 3""";

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "Value 1")
                .expectRow(1, "2", "Value 2")
                .expectRow(2, "3", "Value 3");
    }

    @Test
    void autoEmptyRows() {
        String csv = """
                id,name
                
                1,A\r\r2,B\r
                \r
                3,C""";

        CsvFormat format = CsvFormat.defaultFormat().skipEmptyRows().build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(3)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B")
                .expectRow(2, "3", "C");
    }

    @Test
    void autoComments() {
        String csv = """
                id,name
                # comment with LF
                1,A\r# comment with CR\r2,B\r
                # comment with CRLF\r
                3,C""";

        CsvFormat format = CsvFormat.defaultFormat().enableComments("#").build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(3)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B")
                .expectRow(2, "3", "C");
    }

    @Test
    void quotedLineBreaks() {
        String csv = """
                id,value
                1,"line1
                line2"
                2,"line3\rline4"
                3,"line5\r\nline6"
                """;

        CsvFormat builder = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();
        new DfParserAsserts(csv, builder, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "line1\nline2")
                .expectRow(1, "2", "line3\rline4")
                .expectRow(2, "3", "line5\r\nline6");
    }

    @Test
    void quotedBreaksNoNewline() {
        String csv = """
                id,value
                1,"line1
                line2"
                2,"line3\rline4"
                3,"line5\r\nline6\"""";

        CsvFormat format = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();
        new DfParserAsserts(csv, format, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "line1\nline2")
                .expectRow(1, "2", "line3\rline4")
                .expectRow(2, "3", "line5\r\nline6");
    }

    @Test
    void customQuotedNoNewline() {
        String csv = """
                id,value
                1,"line1
                line2"
                2,"line3\rline4"
                3,"line5\r\nline6\"""";

        CsvFormat format = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).escape(Escape.BACKSLASH).build();
        new DfParserAsserts(csv, format, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "line1\nline2")
                .expectRow(1, "2", "line3\rline4")
                .expectRow(2, "3", "line5\r\nline6");
    }

    @Test
    void autoQuotedLineBreaks() {
        String csv = """
                id,value
                1,"line1
                line2"
                2,"line3\rline4"
                3,"line5\r\nline6"
                """;

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "line1\nline2")
                .expectRow(1, "2", "line3\rline4")
                .expectRow(2, "3", "line5\r\nline6");
    }

    @Test
    void autoQuotedNoNewline() {
        String csv = """
                id,value
                1,"line1
                line2"
                2,"line3\rline4"
                3,"line5\r\nline6\"""";

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectRow(0, "1", "line1\nline2")
                .expectRow(1, "2", "line3\rline4")
                .expectRow(2, "3", "line5\r\nline6");
    }

    @Test
    void autoMalformedAfterQuote() {
        String csv = """
                id,value
                1,"val"ue""";

        assertThrows(IllegalStateException.class, () ->
                new DfParserAsserts(csv, "id", "value"));
    }

    @Test
    void autoCrTrailingRow() {
        String csv = "id,value\r1,Value 1\r2,Value 2\r3,Value 3\r";

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3")
                .expectColumn("value", "Value 1", "Value 2", "Value 3");
    }

    @Test
    void autoTrailingLf() {
        String csv = """
                id,value
                1,Value 1
                2,Value 2
                3,Value 3
                """;

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3")
                .expectColumn("value", "Value 1", "Value 2", "Value 3");
    }

    @Test
    void autoTrailingCrlf() {
        String csv = """
                id,value\r
                1,Value 1\r
                2,Value 2\r
                3,Value 3\r
                """;

        new DfParserAsserts(csv, "id", "value")
                .expectHeight(3)
                .expectColumn("id", "1", "2", "3")
                .expectColumn("value", "Value 1", "Value 2", "Value 3");
    }

    @Test
    void delimiterLineBreakConflict() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .lineBreak(LineBreak.LF)
                .delimiter("\n")
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .lineBreak(LineBreak.CR)
                .delimiter("\r")
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .lineBreak(LineBreak.CRLF)
                .delimiter("\r\n")
                .build());
    }
}
