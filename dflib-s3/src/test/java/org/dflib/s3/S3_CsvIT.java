package org.dflib.s3;

import org.dflib.DataFrame;
import org.dflib.csv.Csv;
import org.dflib.junit.DataFrameAsserts;
import org.dflib.s3.unit.BaseS3Test;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class S3_CsvIT extends BaseS3Test {

    @Test
    void load() {
        DataFrame df = Csv.load(s3("data").source("a.csv"));
        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "x")
                .expectRow(1, "2", "y");
    }

    @Test
    void load_Gzip() {
        DataFrame df = Csv.load(s3("data").source("b.csv.gz"));
        new DataFrameAsserts(df, "A", "B")
                .expectHeight(1)
                .expectRow(0, "3", "z");
    }

    @Test
    void loadAll() {
        Map<String, DataFrame> dfs = Csv.loadAll(s3("data").includeSubfolders().includeExtension("csv").sources());

        assertEquals(2, dfs.size());
        new DataFrameAsserts(dfs.get("a.csv"), "A", "B").expectHeight(2).expectRow(0, "1", "x");
        new DataFrameAsserts(dfs.get("sub/c.csv"), "A", "B").expectHeight(1).expectRow(0, "4", "w");
    }
}
