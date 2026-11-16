package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.RowPredicate;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Parsing errors should report the row and the text of the line where the error occurred.
 */
public class CsvLoader_ErrorLocationTest {

    private static String loadError(CsvLoader loader, String csv) {
        return assertThrows(IllegalStateException.class, () -> loader.load(ByteSource.of(csv.getBytes()))).getMessage();
    }

    @Test
    public void dataAfterClosingQuote() {
        String csv = """
                id,name
                1,"alpha"x
                """;

        assertEquals("Unexpected data after closing quote (row 2): 1,\"alpha\"x",
                loadError(new CsvLoader(), csv));
    }

    @Test
    public void dataAfterClosingQuote_AfterWhitespace() {
        String csv = """
                id,name
                1,"alpha" x
                """;

        assertEquals("Unexpected data after closing quote (row 2): 1,\"alpha\" x",
                loadError(new CsvLoader(), csv));
    }

    @Test
    public void rowNumberWithCrlfSplitAcrossReads() {
        // the CR of the second line is the last char of the first buffer read
        String csv = "h\r\n" + "a".repeat(8 * 1024 - 4) + "\r\nb\r\n\"x\"y\r\n";

        assertEquals("Unexpected data after closing quote (row 4): \"x\"y",
                loadError(new CsvLoader(), csv));
    }

    @Test
    public void dataAfterClosingQuote_FirstColumn() {
        String csv = """
                a,b
                1,2
                "x"y,2
                """;

        assertEquals("Unexpected data after closing quote (row 3): \"x\"y,2",
                loadError(new CsvLoader(), csv));
    }

    @Test
    public void dataAfterClosingQuote_Header() {
        String csv = """
                "a"b,c
                1,2
                """;

        assertEquals("Unexpected data after closing quote (row 1): \"a\"b,c",
                loadError(new CsvLoader(), csv));
    }

    @Test
    public void unclosedQuote() {
        String csv = """
                id,name
                1,"alpha
                """;

        assertEquals("No closing quote found (row 2): 1,\"alpha",
                loadError(new CsvLoader(), csv));
    }

    @Test
    public void shortRow_Primitive() {
        String csv = """
                A,B,C
                0,1,2
                3,4""";

        assertEquals("Can't convert value of column 'C': missing or null value, and no default is set for the int " +
                        "column (row 3): 3,4",
                loadError(new CsvLoader().intCol("C"), csv));
    }

    @Test
    public void rowNumberIncludesSkippedRows() {
        String csv = """
                title
                a,b
                # comment

                1,2
                3,"4"x
                """;

        CsvLoader loader = new CsvLoader()
                .offset(1)
                .format(CsvFormat.defaultFormat().enableComments("#").skipEmptyRows().build());

        assertEquals("Unexpected data after closing quote (row 6): 3,\"4\"x",
                loadError(loader, csv));
    }

    @Test
    public void rowNumberCountsMultilineRowOnce() {
        String csv = """
                a,b
                1,"multi
                line"
                2,"x"y
                """;

        assertEquals("Unexpected data after closing quote (row 3): 2,\"x\"y",
                loadError(new CsvLoader(), csv));
    }

    @Test
    public void longLineIsCut() {
        String csv = "a,b\n" + "x".repeat(100) + ",\"v\"" + "y".repeat(100) + "\n";

        assertEquals("Unexpected data after closing quote (row 2): ..."
                        + "x".repeat(46) + ",\"v\"" + "y".repeat(50) + "...",
                loadError(new CsvLoader(), csv));
    }

    @Test
    public void conversionError() {
        String csv = """
                a,b
                1,2
                x,3
                """;

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> new CsvLoader().intCol("a").load(ByteSource.of(csv.getBytes())));

        assertEquals("Can't convert value of column 'a': Unexpected character for a numeric value: 'x' in x (row 3): x,3",
                e.getMessage());
        assertInstanceOf(NumberFormatException.class, e.getCause().getCause());
    }

    @Test
    public void conversionError_Date() {
        String csv = """
                a,b
                2026-01-01,2
                2026-13-01,3
                """;

        String message = loadError(new CsvLoader().dateCol("a"), csv);
        assertTrue(message.startsWith("Can't convert value of column 'a': "), message);
        assertTrue(message.endsWith(" (row 3): 2026-13-01,3"), message);
    }

    @Test
    public void conversionError_GenerateHeader() {
        String csv = """
                1,x
                1,2
                """;

        assertEquals("Can't convert value of column 'c1': Unexpected character for a numeric value: 'x' (row 1): 1,x",
                loadError(new CsvLoader().generateHeader().longCol(1), csv));
    }

    @Test
    public void conversionError_RowFilter() {
        String csv = """
                a,b
                1,2
                x,3
                """;

        CsvLoader loader = new CsvLoader()
                .intCol("a")
                .rows(RowPredicate.of("b", (String b) -> !b.isEmpty()));

        assertEquals("Can't convert value of column 'a': Unexpected character for a numeric value: 'x' in x (row 3): x,3",
                loadError(loader, csv));
    }

    @Test
    public void conversionError_Sample() {
        String csv = """
                a,b
                1,2
                3,4
                x,5
                """;

        CsvLoader loader = new CsvLoader()
                .intCol("a")
                .rowsSample(10, new Random(1));

        assertEquals("Can't convert value of column 'a': Unexpected character for a numeric value: 'x' in x (row 4): x,5",
                loadError(loader, csv));
    }
}
