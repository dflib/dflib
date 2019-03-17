package com.nhl.dflib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class DataFrame_RenameColumnsTest extends BaseDataFrameTest {

    public DataFrame_RenameColumnsTest(boolean columnar) {
        super(columnar);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{{false}, {true}});
    }

    @Test
    public void testRenameColumns_All() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .renameColumns("c", "d");

        new DFAsserts(df, "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRenameColumns_SizeMismatch() {
        Index i1 = Index.withNames("a", "b");
        createDf(i1,
                1, "x",
                2, "y")
                .renameColumns("c");
    }

    @Test
    public void testRenameColumns_Map() {

        Map<String, String> names = new HashMap<>();
        names.put("b", "c");

        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .renameColumns(names);

        new DFAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }
}
