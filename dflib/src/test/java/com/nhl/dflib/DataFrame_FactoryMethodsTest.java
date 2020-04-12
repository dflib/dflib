package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_FactoryMethodsTest {

    @Test
    public void testNewFrame_Strings() {

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(1, 2);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }
}
