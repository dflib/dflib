package com.nhl.dflib.json;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class JsonLoaderTest {

    @Test
    public void testListOfObjects() {
        String json = "[{\"a\":1, \"b\":\"B\"},{{\"a\":2, \"b\":\"C\"}},{{\"a\":3, \"b\":\"D\"}}]";

        DataFrame df = Json.loader().load(json);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "B")
                .expectRow(1, 2, "C")
                .expectRow(2, 3, "D");
    }

    @Test
    public void testListOfObjects_Sparse() {
        String json = "[{\"a\":1, \"b\":\"B\"},{{\"b\":\"C\"}},{{\"a\":3, \"c\":\"D\"}}]";

        DataFrame df = Json.loader().load(json);
        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "B", null)
                .expectRow(1, null, "C", null)
                .expectRow(2, 3, null, "D");
    }
}
