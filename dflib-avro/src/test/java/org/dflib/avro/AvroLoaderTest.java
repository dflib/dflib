package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvroLoaderTest {

    @Test
    void fromByteSource() {

        DataFrame df = Avro.loader().load(ByteSource.ofUrl(AvroLoaderTest.class.getResource("test1.avro")));

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
    void fromByteSources() {

        ByteSources srcs = ByteSources.of(Map
                .of("f1", ByteSource.ofUrl(AvroLoaderTest.class.getResource("test1.avro")),
                        "f2", ByteSource.ofUrl(AvroLoaderTest.class.getResource("test2.avro"))
                ));

        Map<String, DataFrame> dfs = Avro.loader().loadAll(srcs);
        assertEquals(2, dfs.size());

        new DataFrameAsserts(dfs.get("f1"),  "a", "b")
                .expectHeight(6)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc")
                .expectRow(4, 30000, null)
                .expectRow(5, null, "bc");

        new DataFrameAsserts(dfs.get("f2"), "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "ab")
                .expectRow(1, 40000, "ab")
                .expectRow(2, 40000, "bc")
                .expectRow(3, 30000, "bc");
    }
}
