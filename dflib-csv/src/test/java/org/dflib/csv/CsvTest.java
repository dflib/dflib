package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class CsvTest {

    @Test
    public void fromFile() {
        String csv = """
            A,b,C
            1,2,3
            4,5,6""";

        DataFrame df = Csv.load(ByteSource.of(csv.getBytes()));
        new DataFrameAsserts(df, "A", "b", "C")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }
}
