package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvLoader_BOMTest extends BaseCsvTest {

    @Test
    void bom_unchecked() {
        DataFrame df = new CsvLoader()
                .load(inPath("bom-utf8.csv"));

        // BOM bytes are prepended to the "ID" column name
        assertEquals(3, df.getColumnsIndex().get(0).length());
    }

    @Test
    void checkBOM_OnNonBom() {
        DataFrame df = new CsvLoader()
                .checkByteOrderMark()
                .load(inPath("f1.csv"));

        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    void bomUtf8() {
        DataFrame df = new CsvLoader()
                .checkByteOrderMark()
                .load(inPath("bom-utf8.csv"));

        // BOM bytes should've been removed from the "ID" column name
        assertEquals(2, df.getColumnsIndex().get(0).length());

        new DataFrameAsserts(df, "ID", "START_DATE", "END_DATE")
                .expectHeight(2)
                .expectRow(0, "1", "2008-05-28", "2009-12-14")
                .expectRow(1, "2", "2008-07-01", "2010-01-01");
    }
}
