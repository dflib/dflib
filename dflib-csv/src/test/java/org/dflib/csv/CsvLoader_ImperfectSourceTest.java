package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvLoader_ImperfectSourceTest {
    
    @Test
    public void duplicateColumnNames() {
        byte[] csvBytes = "A,A,A\n1,2,3\n4,5,6".getBytes();
        DataFrame df = new CsvLoader().load(ByteSource.of(csvBytes));

        // if the duplicate column names are present, the following would throw
        assertNotNull(df.getColumn("A"));
        assertNotNull(df.getColumn("A_"));
        assertNotNull(df.getColumn("A__"));

        new DataFrameAsserts(df, "A", "A_", "A__")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

}
