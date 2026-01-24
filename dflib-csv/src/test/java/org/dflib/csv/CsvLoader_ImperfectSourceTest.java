package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvLoader_ImperfectSourceTest {

    static final ByteSource INCOMPLETE_ROWS = ByteSource.of("""
            A,B,C
            0,1,2
            3,4
            5""".getBytes());

    @Test
    public void duplicateColumnNames() {
        String csv = """
                A,A,A
                1,2,3
                4,5,6""";

        DataFrame df = new CsvLoader().load(ByteSource.of(csv.getBytes()));

        // if the duplicate column names are present, the following would throw
        assertNotNull(df.getColumn("A"));
        assertNotNull(df.getColumn("A_"));
        assertNotNull(df.getColumn("A__"));

        new DataFrameAsserts(df, "A", "A_", "A__")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void incompleteRows() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> new CsvLoader().load(INCOMPLETE_ROWS));
    }

    @Test
    public void incompleteRows_nullPadRows() {
        DataFrame df = new CsvLoader()
                .nullPadRows()
                .load(INCOMPLETE_ROWS);

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(3)
                .expectRow(0, "0", "1", "2")
                .expectRow(1, "3", "4", null)
                .expectRow(2, "5", null, null);
    }

    @Test
    public void incompleteRows_nullPadRows_Primitives() {
        assertThrows(IllegalArgumentException.class, () ->
                new CsvLoader().nullPadRows().intCol("C").load(INCOMPLETE_ROWS));
    }

    @Test
    public void incompleteRows_nullPadRows_PrimitiveDefaults() {
        DataFrame df = new CsvLoader()
                .intCol("C", -1)
                .nullPadRows()
                .load(INCOMPLETE_ROWS);

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(3)
                .expectRow(0, "0", "1", 2)
                .expectRow(1, "3", "4", -1)
                .expectRow(2, "5", null, -1);
    }

    @Test
    public void headerMismatch() {
        ByteSource csv = ByteSource.of("""
                0,1
                2,3""".getBytes());

        assertThrows(ArrayIndexOutOfBoundsException.class, () ->
                new CsvLoader().header("A", "B", "C").load(csv));
    }

    @Test
    public void headerMismatch_nullPadRows() {
        ByteSource csv = ByteSource.of("""
                0,1
                2,3""".getBytes());

        DataFrame df = new CsvLoader()
                .header("A", "B", "C")
                .nullPadRows()
                .load(csv);

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(2)
                .expectRow(0, "0", "1", null)
                .expectRow(1, "2", "3", null);
    }
}
