package org.dflib.csv.parser.format;

import org.dflib.csv.parser.CsvParser;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MalformedDataTest {

    @Test
    void unclosedQuoteAtEof() {
        String csv = """
                id,name
                1,"alpha
                """;

        assertThrows(IllegalStateException.class, () -> new CsvParser().parse(new StringReader(csv)));
    }

    @Test
    void unclosedQuoteBeforeRow() {
        String csv = """
                id,name
                1,"alpha
                2,beta
                """;

        assertThrows(IllegalStateException.class, () -> new CsvParser().parse(new StringReader(csv)));
    }

    @Test
    void invalidCharsBeforeEol() {
        String csv = """
                id,name
                1,"alpha"x
                """;

        assertThrows(IllegalStateException.class, () -> new CsvParser().parse(new StringReader(csv)));
    }

    @Test
    void invalidCharsBeforeDelimiter() {
        String csv = """
                id,name,age
                1,"alpha"x,2
                """;

        assertMalformed(csv);
    }

    @Test
    void multiCharDelimiterError() {
        CsvFormat format = CsvFormat.builder()
                .delimiter("||")
                .build();
        String csv = """
                id||name||age
                1||"alpha"x||2
                """;

        assertMalformed(format, csv);
    }

    @Test
    void invalidCharsBeforeCr() {
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.CR)
                .build();
        String csv = "id,name\r1,\"alpha\"x\r";

        assertMalformed(format, csv);
    }

    @Test
    void invalidCharsBeforeCrlf() {
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.CRLF)
                .build();
        String csv = "id,name\r\n1,\"alpha\"x\r\n";

        assertMalformed(format, csv);
    }

    @Test
    void invalidCharsAcrossBuffer() {
        CsvFormat format = CsvFormat.builder()
                .column(CsvFormat.column(0).name("value"))
                .excludeHeaderValues(false)
                .build();
        String payload = "a".repeat(8190);
        String csv = "\"" + payload + "\"x\n";

        assertMalformed(format, csv);
    }

    @Test
    void danglingBackslashEscapeEof() {
        CsvFormat format = CsvFormat.builder()
                .escape(Escape.BACKSLASH)
                .build();
        String csv = "id,name\n1,\"alpha\\\"";

        assertMalformed(format, csv);
    }

    @Test
    void danglingCustomEscapeEof() {
        CsvFormat format = CsvFormat.builder()
                .escape('x')
                .build();
        String csv = "id,name\n1,\"alphax\"";

        assertMalformed(format, csv);
    }

    @Test
    void danglingDoubleEscapeEof() {
        String csv = "id,name\n1,\"alpha\"\"";

        assertMalformed(csv);
    }

    @Test
    void invalidCharsAtEof() {
        String csv = "id,name\n1,\"alpha\"x";

        assertMalformed(csv);
    }

    @Test
    void singleCharDelimiterError() {
        CsvFormat format = CsvFormat.builder()
                .delimiter(";")
                .build();
        String csv = """
                id;name;age
                1;"alpha"x;2
                """;

        assertMalformed(format, csv);
    }

    @Test
    void invalidFirstFieldChars() {
        String csv = """
                id,name
                "alpha"x,1
                """;

        assertMalformed(csv);
    }

    @Test
    void whitespaceBeforeDelimiterError() {
        String csv = """
                id,name,age
                1,"alpha" ,2
                """;

        assertMalformed(csv);
    }

    @Test
    void extraCharsDelimiterError() {
        String csv = """
                id,name,age
                1,"alpha"xyz,2
                """;

        assertMalformed(csv);
    }

    @Test
    void invalidAfterEmptyQuote() {
        String csv = """
                id,name,age
                1,""x,2
                """;

        assertMalformed(csv);
    }

    @Test
    void invalidCharsInHeader() {
        String csv = """
                "id"x,name
                1,alpha
                """;

        assertMalformed(csv);
    }

    @Test
    void validDelimiterAfterQuote() {
        String csv = """
                id,name,age
                1,"alpha",2
                """;

        assertDoesNotThrow(() -> new CsvParser().parse(new StringReader(csv)));
    }

    @Test
    void validBreakAfterQuote() {
        String csv = """
                id,name
                1,"alpha"
                """;

        assertDoesNotThrow(() -> new CsvParser().parse(new StringReader(csv)));
    }

    private static void assertMalformed(String csv) {
        assertThrows(IllegalStateException.class, () -> new CsvParser().parse(new StringReader(csv)));
    }

    private static void assertMalformed(CsvFormat format, String csv) {
        assertThrows(IllegalStateException.class, () -> new CsvParser(format).parse(new StringReader(csv)));
    }
}
