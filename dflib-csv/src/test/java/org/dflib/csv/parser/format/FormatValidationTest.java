package org.dflib.csv.parser.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FormatValidationTest {

    @Test
    void delimiterRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .delimiter(null)
                .build());
    }

    @Test
    void delimiterRejectsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .delimiter("")
                .build());
    }

    @Test
    void delimiterRejectsLineBreaks() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .delimiter("\n")
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .delimiter("|\r|")
                .build());
    }

    @Test
    void commentRejectsEmptyDelimiter() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .enableComments("")
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .delimiter("#")
                .enableComments("#")
                .build());
    }

    @Test
    void commentRejectsLineBreaks() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .enableComments("\n")
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .enableComments("|\r|")
                .build());
    }

    @Test
    void quoteRejectsDelimiterBreak() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .delimiter("\"")
                .quote(Quote.optionalOf('"'))
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .quote(Quote.of('\n'))
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .quote(Quote.of('\r'))
                .build());
    }

    @Test
    void quoteMustBeSpecified() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .quote(null)
                .build());
    }

    @Test
    void quoteEscapeRequired() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .escape(null)
                .build());
    }

    @Test
    void customQuoteEscapeRequired() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .escape(Escape.CUSTOM)
                .build());
    }

    @Test
    void customEscapeRejectsBreak() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .escape('\n')
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .escape('\r')
                .build());
    }

    @Test
    void customEscapeRejectsNul() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .escape('\0')
                .build());
    }

    @Test
    void autoDisabledRequiresColumns() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .autoColumns(false)
                .build());
    }

    @Test
    void invalidNumericSettingsRejected() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .limit(-2)
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .offset(-1)
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .sizeHint(-5)
                .build());
    }

    @Test
    void lineBreakRequired() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.builder()
                .lineBreak(null)
                .build());
    }
}
