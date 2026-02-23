package org.dflib.csv.parser.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FormatValidationTest {

    @Test
    void delimiterRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .delimiter(null)
                .build());
    }

    @Test
    void delimiterRejectsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .delimiter("")
                .build());
    }

    @Test
    void delimiterRejectsLineBreaks() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .delimiter("\n")
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .delimiter("|\r|")
                .build());
    }

    @Test
    void commentRejectsEmptyDelimiter() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .enableComments("")
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .delimiter("#")
                .enableComments("#")
                .build());
    }

    @Test
    void commentRejectsLineBreaks() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .enableComments("\n")
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .enableComments("|\r|")
                .build());
    }

    @Test
    void quoteRejectsDelimiterBreak() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .delimiter("\"")
                .quote(Quote.optionalOf('"'))
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .quote(Quote.of('\n'))
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .quote(Quote.of('\r'))
                .build());
    }

    @Test
    void quoteMustBeSpecified() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .quote(null)
                .build());
    }

    @Test
    void quoteEscapeRequired() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .escape(null)
                .build());
    }

    @Test
    void customQuoteEscapeRequired() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .escape(Escape.CUSTOM)
                .build());
    }

    @Test
    void customEscapeRejectsBreak() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .escape('\n')
                .build());

        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .escape('\r')
                .build());
    }

    @Test
    void customEscapeRejectsNul() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .escape('\0')
                .build());
    }

    @Test
    void lineBreakRequired() {
        assertThrows(IllegalArgumentException.class, () -> CsvFormat.defaultFormat()
                .lineBreak(null)
                .build());
    }
}
