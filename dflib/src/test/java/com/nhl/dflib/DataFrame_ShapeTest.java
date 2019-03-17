package com.nhl.dflib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DataFrame_ShapeTest extends BaseDataFrameTest {

    public DataFrame_ShapeTest(boolean columnar) {
        super(columnar);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{{false}, {true}});
    }

    @Test
    public void testWidth() {
        Index i = Index.withNames("a");
        DataFrame df = createDf(i,
                1,
                2
        );

        assertEquals(1, df.width());
    }

    @Test
    public void testHeight() {
        Index i = Index.withNames("a");
        DataFrame df = createDf(i,
                1,
                2
        );

        assertEquals(2, df.height());
    }
}
