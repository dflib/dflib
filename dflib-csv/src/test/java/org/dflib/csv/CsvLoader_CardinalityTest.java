package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvLoader_CardinalityTest {

    static final ByteSource CSV = ByteSource.of("""
            A,B
            1,ab
            40000,ab
            40000,bc
            30000,bc
            30000,
            ,bc""".getBytes());

    @Test
    public void emptyStringIsNull() {
        DataFrame df = new CsvLoader()
                .col("A", s -> s != null ? Integer.parseInt(s) : null)
                .emptyStringIsNull()
                .load(CSV);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("A").mapVal(System::identityHashCode),
                $col("B").mapVal(System::identityHashCode));

        assertEquals(6, idCardinality.getColumn(0).unique().size());
        assertEquals(6, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void compactCol_Name() {
        DataFrame df = new CsvLoader()
                .compactCol("A", s -> s != null ? Integer.parseInt(s) : null)
                .compactCol("B")
                .emptyStringIsNull()
                .load(CSV);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("A").mapVal(System::identityHashCode),
                $col("B").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }

    @Test
    public void compactCol_Pos() {
        DataFrame df = new CsvLoader()
                .compactCol(0, s -> s != null ? Integer.parseInt(s) : null)
                .compactCol(1)
                .emptyStringIsNull()
                .load(CSV);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        DataFrame idCardinality = df.cols().select(
                $col("A").mapVal(System::identityHashCode),
                $col("B").mapVal(System::identityHashCode));

        assertEquals(4, idCardinality.getColumn(0).unique().size());
        assertEquals(3, idCardinality.getColumn(1).unique().size());
    }
}
