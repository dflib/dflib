package org.dflib.parquet;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class ParquetLoader_ColumnFilterTest {

    static File _4COL_FILE;

    @BeforeAll
    public static void setupCsvDirs() throws URISyntaxException {
        URI csvUri = ParquetLoader_ColumnFilterTest.class.getResource("4col.parquet").toURI();
        _4COL_FILE = new File(csvUri).getAbsoluteFile();
    }

    @Test
    public void all() {
        DataFrame df = Parquet.loader().load(_4COL_FILE);
        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, 2, 3, 4)
                .expectRow(1, 5, 6, 7, 8)
                .expectRow(2, 9, 10, 11, 12);
    }

    @Test
    public void cols() {
        DataFrame df = Parquet.loader()
                .cols("a", "c")
                .load(_4COL_FILE);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 3)
                .expectRow(1, 5, 7)
                .expectRow(2, 9, 11);
    }

    @Test
    public void cols_reorder() {
        DataFrame df = Parquet.loader()
                .cols("c", "a")
                .load(_4COL_FILE);
        new DataFrameAsserts(df, "c", "a")
                .expectHeight(3)
                .expectRow(0, 3, 1)
                .expectRow(1, 7, 5)
                .expectRow(2, 11, 9);
    }

    @Test
    public void cols_pos() {
        DataFrame df = Parquet.loader()
                .cols(0, 2)
                .load(_4COL_FILE);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 3)
                .expectRow(1, 5, 7)
                .expectRow(2, 9, 11);
    }

    @Test
    public void cols_pos_reorder() {
        DataFrame df = Parquet.loader()
                .cols(2, 0)
                .load(_4COL_FILE);
        new DataFrameAsserts(df, "c", "a")
                .expectHeight(3)
                .expectRow(0, 3, 1)
                .expectRow(1, 7, 5)
                .expectRow(2, 11, 9);
    }

    @Test
    public void cols_except() {
        DataFrame df = Parquet.loader()
                .colsExcept("b", "d")
                .load(_4COL_FILE);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 3)
                .expectRow(1, 5, 7)
                .expectRow(2, 9, 11);
    }

    @Test
    public void cols_pos_except() {
        DataFrame df = Parquet.loader()
                .colsExcept(1, 3)
                .load(_4COL_FILE);
        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 1, 3)
                .expectRow(1, 5, 7)
                .expectRow(2, 9, 11);
    }
}
