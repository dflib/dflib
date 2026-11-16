package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Trim;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Rows with fewer values than the header are padded with nulls.
 */
public class CsvLoader_ShortRowsTest {

    @Test
    public void midFile() {
        String csv = """
                a,b,c
                1,2
                3,4,5
                6
                """;

        DataFrame df = new CsvLoader().load(new StringReader(csv));
        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "5")
                .expectRow(2, "6", null, null);
    }

    @Test
    public void emptyValues() {
        // empty values are not missing values
        String csv = """
                a,b,c
                1,,
                2,
                """;

        DataFrame df = new CsvLoader().load(new StringReader(csv));
        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, "1", "", "")
                .expectRow(1, "2", "", null);
    }

    @Test
    public void whitespaceOnlyLine() {
        String csv = "a,b\n1,2\n   \n3,4\n";

        DataFrame df = new CsvLoader().load(new StringReader(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "1", "2")
                .expectRow(1, "   ", null)
                .expectRow(2, "3", "4");
    }

    @Test
    public void quotedLastValue() {
        String csv = "a,b,c\n\"1\"\n\"2\",\"3\"\n";

        DataFrame df = new CsvLoader().load(new StringReader(csv));
        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, "1", null, null)
                .expectRow(1, "2", "3", null);
    }

    @Test
    public void lastRowWithoutLineBreak() {
        String csv = "a,b,c\n1,2,3\n4";

        DataFrame df = new CsvLoader().load(new StringReader(csv));
        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", null, null);
    }

    @ParameterizedTest
    @EnumSource(LineBreak.class)
    public void lineBreaks(LineBreak lineBreak) {
        String nl = switch (lineBreak) {
            case LF -> "\n";
            case CR -> "\r";
            case CRLF, AUTO -> "\r\n";
        };

        String csv = "a,b,c" + nl + "1" + nl + "2,\"x" + nl + "y\"" + nl + "3,4,5" + nl;

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().lineBreak(lineBreak).build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "1", null, null)
                .expectRow(1, "2", "x" + nl + "y", null)
                .expectRow(2, "3", "4", "5");
    }

    @Test
    public void multiCharDelimiter() {
        String csv = "a||b||c\n1||2\n3||4||5\n";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().delimiter("||").build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "5");
    }

    @Test
    public void trimAndNullString() {
        String csv = "a,b,c\n 1 , NA \n2,3,4\n";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().trim(Trim.FULL).build())
                .nullString("NA")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, "1", null, null)
                .expectRow(1, "2", "3", "4");
    }

    @Test
    public void cols() {
        String csv = "a,b,c\n1,2\n3,4,5\n";

        DataFrame df = new CsvLoader().cols("a", "b").load(new StringReader(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void primitiveWithDefault() {
        String csv = "a,b\n1,2\n3\n";

        DataFrame df = new CsvLoader().intCol("a").intCol("b", -1).load(new StringReader(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectIntColumns("a", "b")
                .expectRow(0, 1, 2)
                .expectRow(1, 3, -1);
    }

    @Test
    public void primitiveWithoutDefault() {
        String csv = "a,b\n1,2\n3\n";

        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> new CsvLoader().boolCol("b").load(new StringReader(csv)));

        assertEquals("Can't convert value of column 'b': missing or null value, and no default is set for the " +
                "boolean column (row 3): 3", e.getMessage());
    }

    @Test
    public void primitiveWithoutDefault_NoShortRows() {
        // a primitive column without a default is fine, as long as there are no missing values
        String csv = "a,b\n1,\n3,4\n";

        DataFrame df = new CsvLoader()
                .emptyStringIsNull()
                .intCol("a")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectIntColumns("a")
                .expectRow(0, 1, null)
                .expectRow(1, 3, "4");
    }
}
