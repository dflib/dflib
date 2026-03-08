package org.dflib.echarts.dataframeset;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrameSetTest {

    @Test
    public void height_singleDf() {
        DataFrame df = DataFrame.foldByRow("x", "y").of(1, 2, 3, 4);
        assertEquals(2, DataFrameSet.of(df).height());
    }

    @Test
    public void height_multipleDfs_sameHeight() {
        DataFrame df1 = DataFrame.foldByRow("a").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("b").of(3, 4);
        assertEquals(2, DataFrameSet.of(df1, df2).height());
    }

    @Test
    public void height_multipleDfs_differentHeights() {
        DataFrame df1 = DataFrame.foldByRow("a").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("b").of(3, 4, 5);
        assertThrows(IllegalStateException.class, () -> DataFrameSet.of(df1, df2).height());
    }

    @Test
    public void maxHeight_singleDf() {
        DataFrame df = DataFrame.foldByRow("x").of(1, 2, 3);
        assertEquals(3, DataFrameSet.of(df).maxHeight());
    }

    @Test
    public void maxHeight_multipleDfs() {
        DataFrame df1 = DataFrame.foldByRow("a").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("b").of(3, 4, 5);
        assertEquals(3, DataFrameSet.of(df1, df2).maxHeight());
    }

    @Test
    public void singleDf_unqualifiedName() {
        DataFrame df = DataFrame.foldByRow("x", "y").of(1, 2, 3, 4);
        DataFrameSet set = DataFrameSet.of(df);

        assertSame(df.getColumn("x"), set.getColumn("x"));
        assertSame(df.getColumn("y"), set.getColumn("y"));
    }

    @Test
    public void twoDfs_uniqueColumns() {
        DataFrame df1 = DataFrame.foldByRow("a", "b").of(1, 2, 3, 4);
        DataFrame df2 = DataFrame.foldByRow("c", "d").of(5, 6, 7, 8);
        DataFrameSet set = DataFrameSet.of(df1, df2);

        assertSame(df1.getColumn("a"), set.getColumn("a"));
        assertSame(df1.getColumn("b"), set.getColumn("b"));
        assertSame(df2.getColumn("c"), set.getColumn("c"));
        assertSame(df2.getColumn("d"), set.getColumn("d"));
    }

    @Test
    public void twoDfs_conflictingColumnName() {
        DataFrame df1 = DataFrame.foldByRow("x", "y").of(1, 2, 3, 4);
        DataFrame df2 = DataFrame.foldByRow("x", "y").of(5, 6, 7, 8);
        DataFrameSet set = DataFrameSet.of(df1, df2);

        assertSame(df1.getColumn("x"), set.getColumn("x"));
        assertSame(df1.getColumn("y"), set.getColumn("y"));
        assertSame(df2.getColumn("x"), set.getColumn("x_"));
        assertSame(df2.getColumn("y"), set.getColumn("y_"));
    }

    @Test
    public void threeDfs_conflictingColumnName() {
        DataFrame df1 = DataFrame.foldByRow("x").of(1, 2);
        DataFrame df2 = DataFrame.foldByRow("x").of(3, 4);
        DataFrame df3 = DataFrame.foldByRow("x").of(5, 6);
        DataFrameSet set = DataFrameSet.of(df1, df2, df3);

        assertSame(df1.getColumn("x"), set.getColumn("x"));
        assertSame(df2.getColumn("x"), set.getColumn("x_"));
        assertSame(df3.getColumn("x"), set.getColumn("x__"));
    }

    @Test
    public void namedDf_qualifiedName() {
        DataFrame df = DataFrame.foldByRow("x").of(1, 2).as("s1");
        DataFrameSet set = DataFrameSet.of(df);

        assertSame(df.getColumn("x"), set.getColumn("s1.x"));
        assertSame(df.getColumn("x"), set.getColumn("x"));
    }

    @Test
    public void qualifiedNameConflictsWithUnqualified_qualifiedFirst() {
        DataFrame df1 = DataFrame.foldByRow("x").of(1, 2).as("s1");
        DataFrame df2 = DataFrame.foldByRow("s1.x").of(3, 4);
        DataFrameSet set = DataFrameSet.of(df1, df2);

        assertSame(df1.getColumn("x"), set.getColumn("s1.x"));
        assertSame(df1.getColumn("x"), set.getColumn("x"));
        assertSame(df2.getColumn("s1.x"), set.getColumn("s1.x_"));
    }

    @Test
    public void qualifiedNameConflictsWithUnqualified_unqualifiedFirst() {
        DataFrame df1 = DataFrame.foldByRow("s1.x").of(3, 4);
        DataFrame df2 = DataFrame.foldByRow("x").of(1, 2).as("s1");
        DataFrameSet set = DataFrameSet.of(df1, df2);

        assertSame(df1.getColumn("s1.x"), set.getColumn("s1.x"));
        assertSame(df2.getColumn("x"), set.getColumn("x"));
        assertSame(df2.getColumn("x"), set.getColumn("s1.x_"));
    }

    @Test
    public void unknownName_throwsException() {
        DataFrame df = DataFrame.foldByRow("x").of(1, 2);
        DataFrameSet set = DataFrameSet.of(df);

        assertThrows(IllegalArgumentException.class, () -> set.getColumn("z"));
    }
}
