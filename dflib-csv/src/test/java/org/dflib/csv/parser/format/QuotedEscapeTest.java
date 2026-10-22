package org.dflib.csv.parser.format;

import org.dflib.DataFrame;
import org.dflib.csv.Csv;
import org.dflib.csv.parser.test.DfParserAsserts;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

class QuotedEscapeTest {

    private static final CsvFormat BACKSLASH = CsvFormat.defaultFormat().escape(Escape.BACKSLASH).build();
    private static final CsvFormat CUSTOM = CsvFormat.defaultFormat().escape('$').build();

    @Test
    void escapedEscape() {
        String csv = "id,name\n1,\"a\\\\,b\"\n";
        new DfParserAsserts(csv, BACKSLASH, "id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "a\\,b");
    }

    @Test
    void escapedEscapeBeforeClosingQuote() {
        String csv = "id,name\n1,\"x,y\\\\\"\n";
        new DfParserAsserts(csv, BACKSLASH, "id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "x,y\\");
    }

    @Test
    void escapedQuoteThenEscapedEscape() {
        String csv = "id,name\n1,\"q\\\"\\\\\"\n";
        new DfParserAsserts(csv, BACKSLASH, "id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "q\"\\");
    }

    @Test
    void onlyEscapedEscape() {
        String csv = "id,name\n1,\"\\\\\"\n";
        new DfParserAsserts(csv, BACKSLASH, "id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "\\");
    }

    @Test
    void nonMetaEscapeKept() {
        String csv = "id,name\n1,\"a\\,b\"\n";
        new DfParserAsserts(csv, BACKSLASH, "id", "name")
                .expectHeight(1)
                .expectRow(0, "1", "a\\,b");
    }

    @Test
    void escapedEscapeInHeader() {
        String csv = "\"na\\\\me\",x\n1,2\n";
        new DfParserAsserts(csv, BACKSLASH, "na\\me", "x")
                .expectHeight(1)
                .expectRow(0, "1", "2");
    }

    @Test
    void customEscapedEscape() {
        String csv = "id,name\n1,\"a$$,b\"\n2,\"a$\"$$\"\n";
        new DfParserAsserts(csv, CUSTOM, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "a$,b")
                .expectRow(1, "2", "a\"$");
    }

    @Test
    void escapedEscapeInSkippedOffsetRow() {
        String csv = "1,\"a\\\\\"\nid,name\n2,b\n";
        DataFrame df = Csv.loader().format(BACKSLASH).offset(1).load(new StringReader(csv));
        new DataFrameAsserts(df, "id", "name")
                .expectHeight(1)
                .expectRow(0, "2", "b");
    }

    @Test
    void danglingEscapeAtEof() {
        assertThrows(IllegalStateException.class,
                () -> Csv.loader().format(BACKSLASH).load(new StringReader("id,name\n1,\"abc\\\\")));
        assertThrows(IllegalStateException.class,
                () -> Csv.loader().format(BACKSLASH).load(new StringReader("id,name\n1,\"abc\\")));
    }

    @Test
    void unescapeDoesNotClobberOtherColumns() {
        String escaped = "\\\"".repeat(50);
        String csv = "a,b\nab," + "\"" + escaped + "\"\n";
        DataFrame df = Csv.loader().format(BACKSLASH).cols(1, 0).load(new StringReader(csv));
        new DataFrameAsserts(df, "b", "a")
                .expectHeight(1)
                .expectRow(0, "\"".repeat(50), "ab");
    }
}
