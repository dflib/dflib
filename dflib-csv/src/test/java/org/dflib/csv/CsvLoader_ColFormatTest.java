package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Quote;
import org.dflib.csv.parser.format.Trim;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

class CsvLoader_ColFormatTest {

    @Test
    void colFormatByName_nullString_withIntDefault_keepsIntType() {
        String csv = "A,B\n1,ok\n..,x";

        DataFrame df = new CsvLoader()
                .intCol("A", 0)
                .colFormat("A", CsvFormat.columnFormat().nullString("..").build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectIntColumns(0)
                .expectRow(0, 1, "ok")
                .expectRow(1, 0, "x");
    }

    @Test
    void colFormatByIndex_nullString_withIntDefault_keepsIntType() {
        String csv = "A,B\n1,ok\n..,x";

        DataFrame df = new CsvLoader()
                .intCol(0, 0)
                .colFormat(0, CsvFormat.columnFormat().nullString("..").build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectIntColumns(0)
                .expectRow(0, 1, "ok")
                .expectRow(1, 0, "x");
    }

    @Test
    void colFormatByName_overridesGlobalTrim_forOneColumnOnly() {
        String csv = "A,B\n  a  ,  b  ";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().trim(Trim.FULL).build())
                .colFormat("A", CsvFormat.columnFormat().trim(Trim.NONE).build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(1)
                .expectRow(0, "  a  ", "b");
    }

    @Test
    void colFormatByIndex_overridesGlobalQuote_forOneColumnOnly() {
        String csv = "A,B\n\"1\",\"x\"";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().quote(Quote.optionalOf('"')).build())
                .colFormat(1, CsvFormat.columnFormat().quote(Quote.none()).build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(1)
                .expectRow(0, "1", "\"x\"");
    }

    @Test
    void colFormat_partialFormat_inheritsUnspecifiedGlobalSettings() {
        String csv = "A,B\n 1 ,ok\n NA ,x";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().trim(Trim.FULL).build())
                .intCol("A", -1)
                .colFormat("A", CsvFormat.columnFormat().nullString("NA").build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectIntColumns(0)
                .expectRow(0, 1, "ok")
                .expectRow(1, -1, "x");
    }

    @Test
    void colFormat_withIntCol_isOrderInsensitive() {
        String csv = "A\n..\n1";

        DataFrame dfIntThenFormat = new CsvLoader()
                .intCol("A", 0)
                .colFormat("A", CsvFormat.columnFormat().nullString("..").build())
                .load(new StringReader(csv));

        new DataFrameAsserts(dfIntThenFormat, "A")
                .expectHeight(2)
                .expectIntColumns(0)
                .expectRow(0, 0)
                .expectRow(1, 1);

        // reversed order should produce the same result
        DataFrame dfFormatThenInt = new CsvLoader()
                .colFormat("A", CsvFormat.columnFormat().nullString("..").build())
                .intCol("A", 0)
                .load(new StringReader(csv));

        new DataFrameAsserts(dfFormatThenInt, "A")
                .expectHeight(2)
                .expectIntColumns(0)
                .expectRow(0, 0)
                .expectRow(1, 1);
    }

    @Test
    void colFormat_lastCallWins_forSameColumn() {
        String csv = "A\nX\nY";

        DataFrame df = new CsvLoader()
                .nullString("unused")
                .colFormat("A", CsvFormat.columnFormat().nullString("X").build())
                .colFormat("A", CsvFormat.columnFormat().nullString("Y").build())
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A")
                .expectHeight(2)
                .expectRow(0, "X")
                .expectRow(1, null);
    }
}
