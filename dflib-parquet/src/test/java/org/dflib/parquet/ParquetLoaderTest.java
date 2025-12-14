package org.dflib.parquet;

import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParquetLoaderTest {

    @Test
    public void fromByteSource() {
        DataFrame df = Parquet.loader().load(ByteSource.ofUrl(getClass().getResource("test1.parquet")));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");
    }


    @Test
    public void fromByteSources() {
        Map<String, DataFrame> dfs = Parquet
                .loader()
                .loadAll(ByteSources.of(Map.of(
                        "f1", ByteSource.ofUrl(getClass().getResource("test1.parquet")),
                        "f2", ByteSource.ofUrl(getClass().getResource("4col.parquet"))
                )));

        assertEquals(2, dfs.size());

        new DataFrameAsserts(dfs.get("f1"), "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        new DataFrameAsserts(dfs.get("f2"), "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, 2, 3, 4)
                .expectRow(1, 5, 6, 7, 8)
                .expectRow(2, 9, 10, 11, 12);
    }
}
