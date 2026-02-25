package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.RowPredicate;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvLoader_ConfigCombinationsTest {

    // --- type conversions + column selection ---

    @Test
    void intCol_cols() {
        String csv = "A,B,C\n1,2,3\n4,5,6";

        DataFrame df = new CsvLoader()
                .intCol("A")
                .intCol("C")
                .cols("C", "A")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "C", "A")
                .expectHeight(2)
                .expectRow(0, 3, 1)
                .expectRow(1, 6, 4);
    }

    @Test
    void intCol_format() {
        String csv = "A,B\n1,2\n..,5";

        DataFrame df = new CsvLoader()
                .colFormat("A", CsvFormat.columnFormat().nullString("..").build())
                .intCol("A", 0)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 1, "2")
                .expectRow(1, 0, "5");
    }

    @Test
    void intCol_colsExcept() {
        String csv = "A,B,C\n1,2,3\n4,5,6";

        DataFrame df = new CsvLoader()
                .intCol("A")
                .colsExcept("B")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, 1, "3")
                .expectRow(1, 4, "6");
    }

    @Test
    void doubleCol_cols() {
        String csv = "A,B,C\n1.1,2.2,3.3\n4.4,5.5,6.6";

        DataFrame df = new CsvLoader()
                .doubleCol("B")
                .cols("B")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "B")
                .expectHeight(2)
                .expectRow(0, 2.2)
                .expectRow(1, 5.5);
    }

    // --- type conversions + offset/limit ---

    @Test
    void intCol_offset() {
        String csv = "A,B\n1,2\n3,4\n5,6";

        DataFrame df = new CsvLoader()
                .intCol(0)
                .intCol(1)
                .offset(1)
                .load(new StringReader(csv));

        // offset skips raw rows, so "A,B" is skipped, "1,2" becomes header
        new DataFrameAsserts(df, "1", "2")
                .expectHeight(2)
                .expectRow(0, 3, 4)
                .expectRow(1, 5, 6);
    }

    @Test
    void intCol_offset_named() {
        String csv = "A,B\n1,2\n3,4\n5,6";
        assertThrows(IllegalStateException.class, () -> new CsvLoader()
                .intCol("A")
                .intCol("B")
                .offset(1)
                .load(new StringReader(csv)));
    }

    @Test
    void intCol_limit() {
        String csv = "A,B\n1,2\n3,4\n5,6";

        DataFrame df = new CsvLoader()
                .intCol("A")
                .limit(2)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 1, "2")
                .expectRow(1, 3, "4");
    }

    @Test
    void intCol_offset_limit() {
        String csv = "A,B\n1,2\n3,4\n5,6\n7,8";

        DataFrame df = new CsvLoader()
                .intCol(1)
                .offset(1)
                .limit(1)
                .load(new StringReader(csv));

        // offset skips "A,B", header becomes "1,2", limit 1 data row
        new DataFrameAsserts(df, "1", "2")
                .expectHeight(1)
                .expectRow(0, "3", 4);
    }

    // --- type conversions + null handling ---

    @Test
    void intCol_emptyNull_noDefault() {
        String csv = "A,B\n1,\n,4";

        assertThrows(IllegalArgumentException.class, () -> new CsvLoader()
                .emptyStringIsNull()
                .intCol("A")
                .load(new StringReader(csv)));
    }

    @Test
    void intCol_emptyNull_withDefault() {
        String csv = "A,B\n1,\n,4";

        DataFrame df = new CsvLoader()
                .emptyStringIsNull()
                .intCol("A", -1)
                .intCol("B", -2)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 1, -2)
                .expectRow(1, -1, 4);
    }

    @Test
    void nullString_cols() {
        String csv = "A,B,C\n1,N/A,3\nN/A,5,N/A";

        DataFrame df = new CsvLoader()
                .nullString("N/A")
                .cols("A", "C")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, "1", "3")
                .expectRow(1, null, null);
    }

    @Test
    void doubleCol_null_withDefault() {
        String csv = "A,B\n1.1,N/A\nN/A,4.4";

        DataFrame df = new CsvLoader()
                .nullString("N/A")
                .doubleCol("A", -1.0)
                .doubleCol("B", -2.0)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 1.1, -2.0)
                .expectRow(1, -1.0, 4.4);
    }

    @Test
    void longCol_emptyNull_withDefault() {
        String csv = "A,B\n100,\n,200";

        DataFrame df = new CsvLoader()
                .emptyStringIsNull()
                .longCol("A", -1L)
                .longCol("B", -2L)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 100L, -2L)
                .expectRow(1, -1L, 200L);
    }

    // --- compactCol + column selection ---

    @Test
    void compactCol_cols() {
        String csv = "A,B,C\nX,1,P\nX,2,Q\nY,3,P";

        DataFrame df = new CsvLoader()
                .compactCol("A")
                .cols("A", "B")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(3)
                .expectRow(0, "X", "1")
                .expectRow(1, "X", "2")
                .expectRow(2, "Y", "3");
    }

    @Test
    void compactCol_colsExcept() {
        String csv = "A,B,C\nX,1,P\nX,2,P\nY,3,Q";

        DataFrame df = new CsvLoader()
                .compactCol("C")
                .colsExcept("B")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "C")
                .expectHeight(3)
                .expectRow(0, "X", "P")
                .expectRow(1, "X", "P")
                .expectRow(2, "Y", "Q");
    }

    // --- nullPadRows + column selection ---

    @Test
    void nullPadRows_cols() {
        String csv = "A,B,C\n1,2,3\n4";

        DataFrame df = new CsvLoader()
                .nullPadRows()
                .cols("A", "C")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, "1", "3")
                .expectRow(1, "4", null);
    }

    @Test
    void nullPadRows_colsExcept() {
        String csv = "A,B,C\n1,2,3\n4";

        DataFrame df = new CsvLoader()
                .nullPadRows()
                .colsExcept("B")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, "1", "3")
                .expectRow(1, "4", null);
    }

    @Test
    void nullPadRows_intCol_cols() {
        String csv = "A,B,C\n1,2,3\n4";

        DataFrame df = new CsvLoader()
                .nullPadRows()
                .intCol("A", 0)
                .cols("A", "C")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "C")
                .expectHeight(2)
                .expectRow(0, 1, "3")
                .expectRow(1, 4, null);
    }

    // --- nullPadRows + rows predicate ---

    @Test
    void nullPadRows_rows() {
        String csv = "A,B\n1,2\n3\n5,6";

        DataFrame df = new CsvLoader()
                .nullPadRows()
                .emptyStringIsNull()
                .rows(RowPredicate.of("B", (String s) -> s != null))
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "5", "6");
    }

    // --- sampling + column selection ---

    @Test
    void sampling_cols() {
        String csv = "A,B,C\n1,2,3\n4,5,6\n7,8,9\n10,11,12\n13,14,15";

        DataFrame df = new CsvLoader()
                .cols("C", "A")
                .rowsSample(2, new Random(42))
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "C", "A")
                .expectHeight(2);
    }

    @Test
    void sampling_offset() {
        String csv = "A,B\n1,2\n3,4\n5,6\n7,8\n9,10";

        DataFrame df = new CsvLoader()
                .offset(1)
                .rowsSample(2, new Random(42))
                .load(new StringReader(csv));

        // offset skips "A,B", header becomes "1,2"
        new DataFrameAsserts(df, "1", "2")
                .expectHeight(2);
    }

    @Test
    void sampling_limit() {
        String csv = "A,B\n1,2\n3,4\n5,6\n7,8\n9,10";

        DataFrame df = new CsvLoader()
                .limit(3)
                .rowsSample(2, new Random(42))
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2);
    }

    // --- rows predicate + offset/limit ---

    @Test
    void rows_offset() {
        String csv = "A,B\n1,10\n2,20\n3,30\n4,40";

        DataFrame df = new CsvLoader()
                .intCol(0)
                .offset(1)
                .rows(RowPredicate.of(0, (Integer i) -> i > 2))
                .load(new StringReader(csv));

        // offset skips "A,B", header becomes "1,10", data rows: 2,20 / 3,30 / 4,40
        new DataFrameAsserts(df, "1", "10")
                .expectHeight(2)
                .expectRow(0, 3, "30")
                .expectRow(1, 4, "40");
    }

    @Test
    void rows_limit() {
        String csv = "A,B\n1,10\n2,20\n3,30\n4,40\n5,50";

        DataFrame df = new CsvLoader()
                .intCol("A")
                .rows(RowPredicate.of("A", (Integer i) -> i % 2 == 0))
                .limit(3)
                .load(new StringReader(csv));

        // limit 3 → first 3 data rows (1,2,3), filter even → only "2"
        new DataFrameAsserts(df, "A", "B")
                .expectHeight(1)
                .expectRow(0, 2, "20");
    }

    @Test
    void rows_genHeader() {
        String csv = "1,10\n2,20\n3,30";

        DataFrame df = new CsvLoader()
                .generateHeader()
                .intCol("c0")
                .rows(RowPredicate.of("c0", (Integer i) -> i > 1))
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, 2, "20")
                .expectRow(1, 3, "30");
    }

    // --- offset + header modes ---

    @Test
    void offset_header() {
        String csv = "garbage\n1,2\n3,4\n5,6";

        DataFrame df = new CsvLoader()
                .offset(1)
                .header("X", "Y")
                .load(new StringReader(csv));

        // offset skips "garbage", header("X","Y") treats remaining as data
        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(3)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4")
                .expectRow(2, "5", "6");
    }

    @Test
    void offset_generateHeader() {
        String csv = "garbage\n1,2\n3,4";

        DataFrame df = new CsvLoader()
                .offset(1)
                .generateHeader()
                .load(new StringReader(csv));

        // offset skips "garbage", generateHeader treats all remaining as data
        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    // --- cols override behavior ---

    @Test
    void cols_overridePrevCols() {
        String csv = "A,B,C\n1,2,3\n4,5,6";

        DataFrame df = new CsvLoader()
                .cols("A", "B")
                .cols("B", "C")
                .load(new StringReader(csv));

        // last cols() call should win
        new DataFrameAsserts(df, "B", "C")
                .expectHeight(2)
                .expectRow(0, "2", "3")
                .expectRow(1, "5", "6");
    }

    // --- col by index (only col by name is tested in CsvLoaderNewTest) ---

    @Test
    void col_index() {
        String csv = "A,B\n1,2\n3,4";

        DataFrame df = new CsvLoader()
                .col(0, s -> Integer.parseInt(s) * 10)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, 10, "2")
                .expectRow(1, 30, "4");
    }

    // --- type conversion + generateHeader ---

    @Test
    void intCol_generateHeader() {
        String csv = "1,2\n3,4";

        DataFrame df = new CsvLoader()
                .generateHeader()
                .intCol("c0")
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, 1, "2")
                .expectRow(1, 3, "4");
    }
}
