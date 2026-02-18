package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class QuoteTest {

    @Test
    void quotesEmbeddedAndEscaped() {
        String csv = """
                id,name,value
                1,"Name, with comma",Value 1
                2,Name 2,"A ""quoted"" value"
                3,"Name 3","Value with ""escaped"" quotes"
                """;

        new DfParserAsserts(csv, "id", "name", "value")
                .expectHeight(3);
    }

    @Test
    void quotesOptionalTrim() {
        String csv = """
                id,name,value
                1,  Name  ,  Value
                2, "Quoted" , "Also"
                """;

        CsvFormat format = CsvFormat.builder()
                .trim(Trim.FULL)
                .build();

        new DfParserAsserts(csv, format, "id", "name", "value")
                .expectRow(0, "1", "Name", "Value")
                .expectRow(1, "2", "\"Quoted\"", "\"Also\"");
    }

    @Test
    void quotedValues() {
        String csv = """
                "id","name",value
                "1","Name 1, with comma",Value 1
                "2","Name 2","Value 2"
                "3","Name 3","Value with ""escaped"" quotes"
                """;

        CsvFormat format = CsvFormat.builder()
                .column(CsvFormat.column("id").quote(Quote.of('"')).type(CsvColumnType.INTEGER))
                .column(CsvFormat.column("name").quote(Quote.of('"')))
                .column(CsvFormat.column("value").quote(Quote.optionalOf('"')))
                .build();

        new DfParserAsserts(csv, format,"id", "name", "value")
                .expectHeight(3)
                .expectRow(0, 1, "Name 1, with comma", "Value 1")
                .expectRow(1, 2, "Name 2", "Value 2")
                .expectRow(2, 3, "Name 3", "Value with \"escaped\" quotes");
    }

    @Test
    void unquotedValues() {
        String csv = """
                id,name,value
                1,Name 1,Value 1
                2,Name 2,"Value 2"
                3,Name 3,"Value with ""escaped"" quotes"
                """;

        CsvFormat format = CsvFormat.builder()
                .column(CsvFormat.column("id").quote(Quote.none()).type(CsvColumnType.INTEGER))
                .column(CsvFormat.column("name").quote(Quote.none()))
                .column(CsvFormat.column("value").quote(Quote.optionalOf('"')))
                .build();

        new DfParserAsserts(csv, format,"id", "name", "value")
                .expectHeight(3)
                .expectRow(0, 1, "Name 1", "Value 1")
                .expectRow(1, 2, "Name 2", "Value 2")
                .expectRow(2, 3, "Name 3", "Value with \"escaped\" quotes");
    }

    @Test
    void perColumnQuote() {
        String csv = """
                id,name
                1,"A"
                """;

        CsvFormat format = CsvFormat.builder()
                .quote(Quote.optionalOf('"'))
                .column(CsvFormat.column("id"))
                .column(CsvFormat.column("name").quote(Quote.none()))
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "\"A\"");
    }

    @Test
    void backslashQuoteEscape() {
        String csv = """
                id,name
                1,"A \\"B"
                """;

        CsvFormat format = CsvFormat.builder()
                .escape(Escape.BACKSLASH)
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "A \"B");
    }

    @Test
    void customQuoteEscape() {
        String csv = """
                id,name
                1,"A x"B"
                """;

        CsvFormat format = CsvFormat.builder()
                .escape('x')
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "A \"B");
    }

    @Test
    void quotedHeaderAndValues() {
        String csv = """
                "first,name","status""code"
                "a,b","ok""1"
                "x","ready"
                """;

        new DfParserAsserts(csv, "first,name", "status\"code")
                .expectHeight(2)
                .expectRow(0, "a,b", "ok\"1")
                .expectRow(1, "x", "ready");
    }

    @Test
    void lineBreakInsideQuoted() {
        String csv = "id,text\r\n1,\"line1\r\nline2\"\r\n2,\"x\"\r\n";

        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.CRLF)
                .build();

        new DfParserAsserts(csv, format, "id", "text")
                .expectHeight(2)
                .expectRow(0, "1", "line1\r\nline2")
                .expectRow(1, "2", "x");
    }

    @Test
    void commentInsideQuoted() {
        String csv = """
                id,text
                1,"#not-a-comment"
                #real-comment
                2,"value"
                """;

        CsvFormat format = CsvFormat.builder()
                .enableComments("#")
                .build();

        new DfParserAsserts(csv, format, "id", "text")
                .expectHeight(2)
                .expectRow(0, "1", "#not-a-comment")
                .expectRow(1, "2", "value");
    }

    @Test
    void multiDelimiterInQuoted() {
        String csv = """
                id||text
                1||"a||b"
                2||c
                """;

        CsvFormat format = CsvFormat.builder()
                .delimiter("||")
                .build();

        new DfParserAsserts(csv, format, "id", "text")
                .expectHeight(2)
                .expectRow(0, "1", "a||b")
                .expectRow(1, "2", "c");
    }
}
