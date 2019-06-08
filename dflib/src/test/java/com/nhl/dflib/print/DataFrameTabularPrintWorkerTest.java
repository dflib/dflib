package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataFrameTabularPrintWorkerTest {

    private DataFrame df;

    @Before
    public void initDataFrameParts() {
        this.df = DataFrame
                .newFrame("col1", "column2")
                .columns(Series.forData("one", "two", "three", "four"), IntSeries.forInts(1, 2, 3, 4));
    }

    @Test
    public void testAppendFixedWidth() {
        assertEquals("a  ", new DataFrameTabularPrintWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 3, "%1$-3s").toString());
        assertEquals("a ", new DataFrameTabularPrintWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 2, "%1$-2s").toString());
        assertEquals("a", new DataFrameTabularPrintWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 1, "%1$-1s").toString());
        assertEquals("..", new DataFrameTabularPrintWorker(new StringBuilder(), 3, 20).appendFixedWidth("abc", 2, "%1$-2s").toString());
    }

    @Test
    public void testPrint_Full() {
        DataFrameTabularPrintWorker w = new DataFrameTabularPrintWorker(new StringBuilder(), 5, 10);

        assertEquals(System.lineSeparator() +
                "col1  column2" + System.lineSeparator() +
                "----- -------" + System.lineSeparator() +
                "one         1" + System.lineSeparator() +
                "two         2" + System.lineSeparator() +
                "three       3" + System.lineSeparator() +
                "four        4" + System.lineSeparator() +
                "4 rows x 2 columns", w.print(df).toString());
    }

    @Test
    public void testPrint_TruncateRows() {
        DataFrameTabularPrintWorker w = new DataFrameTabularPrintWorker(new StringBuilder(), 2, 10);

        assertEquals(System.lineSeparator() +
                "col1 column2" + System.lineSeparator() +
                "---- -------" + System.lineSeparator() +
                "one        1" + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "four       4" + System.lineSeparator() +
                "4 rows x 2 columns", w.print(df).toString());
    }

    @Test
    public void testPrint_TruncateColumns() {
        DataFrameTabularPrintWorker w = new DataFrameTabularPrintWorker(new StringBuilder(), 5, 4);

        assertEquals(System.lineSeparator() +
                "col1 c..2" + System.lineSeparator() +
                "---- ----" + System.lineSeparator() +
                "one     1" + System.lineSeparator() +
                "two     2" + System.lineSeparator() +
                "t..e    3" + System.lineSeparator() +
                "four    4" + System.lineSeparator() +
                "4 rows x 2 columns", w.print(df).toString());
    }
}
