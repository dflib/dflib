package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class UnquotedEscapeTest {

    @Test
    void escapedTabNoQuotes() {
        CsvFormat format = CsvFormat.builder()
                .delimiter("\t")
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();

        String csv = "A\tB\n1\t2\\\t3";

        new DfParserAsserts(csv, format, "A", "B")
                .expectRow(0, "1", "2\t3");
    }

    @Test
    void escapedCustomNoQuotes() {
        CsvFormat format = CsvFormat.builder()
                .delimiter(",")
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape('$')
                .build();

        String csv = "A,B\n1,2$,3";

        new DfParserAsserts(csv, format, "A", "B")
                .expectRow(0, "1", "2,3");
    }

    @Test
    void escapedMultiDelimiter() {
        CsvFormat format = CsvFormat.builder()
                .delimiter("||")
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();

        String csv = "A||B\n1||2\\||3";

        new DfParserAsserts(csv, format, "A", "B")
                .expectRow(0, "1", "2||3");
    }

    @Test
    void nonDelimiterEscapeRemoved() {
        CsvFormat format = CsvFormat.builder()
                .delimiter(",")
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();

        String csv = "A,B\n1,a\\bc";

        new DfParserAsserts(csv, format, "A", "B")
                .expectRow(0, "1", "abc");
    }

    @Test
    void trailingEscapeIsPreserved() {
        CsvFormat format = CsvFormat.builder()
                .delimiter(",")
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();

        String csv = "A,B\n1,abc\\";

        new DfParserAsserts(csv, format, "A", "B")
                .expectRow(0, "1", "abc\\");
    }

    @Test
    void noEscapeKeepsBackslash() {
        CsvFormat format = CsvFormat.builder()
                .delimiter(",")
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape(Escape.NONE)
                .build();

        String csv = "A,B\n1,a\\bc";

        new DfParserAsserts(csv, format, "A", "B")
                .expectRow(0, "1", "a\\bc");
    }
}
