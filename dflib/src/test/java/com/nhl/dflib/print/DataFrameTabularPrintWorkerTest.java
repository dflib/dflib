package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataFrameTabularPrintWorkerTest {

    private DataFrame df;

    @Before
    public void initDataFrameParts() {

        Index columns = Index.forLabels("col1", "column2");
        this.df = DataFrame.forRows(columns,
                DataFrame.row("one", 1),
                DataFrame.row("two", 2),
                DataFrame.row("three", 3),
                DataFrame.row("four", 4));
    }

    @Test
    public void testAppendFixedWidth() {
        assertEquals("a  ", new DataFrameTabularPrintWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 3).toString());
        assertEquals("a ", new DataFrameTabularPrintWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 2).toString());
        assertEquals("a", new DataFrameTabularPrintWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 1).toString());
        assertEquals("..", new DataFrameTabularPrintWorker(new StringBuilder(), 3, 20).appendFixedWidth("abc", 2).toString());
    }

    @Test
    public void testPrint_Full() {
        DataFrameTabularPrintWorker w = new DataFrameTabularPrintWorker(new StringBuilder(), 5, 10);

        assertEquals("" +
                "col1  column2" + System.lineSeparator() +
                "----- -------" + System.lineSeparator() +
                "one   1      " + System.lineSeparator() +
                "two   2      " + System.lineSeparator() +
                "three 3      " + System.lineSeparator() +
                "four  4      " + System.lineSeparator() +
                "4 rows x 2 columns", w.print(df).toString());
    }

    @Test
    public void testPrint_TruncateRows() {
        DataFrameTabularPrintWorker w = new DataFrameTabularPrintWorker(new StringBuilder(), 2, 10);

        assertEquals("" +
                "col1 column2" + System.lineSeparator() +
                "---- -------" + System.lineSeparator() +
                "one  1      " + System.lineSeparator() +
                "two  2      " + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "4 rows x 2 columns", w.print(df).toString());
    }

    @Test
    public void testPrint_TruncateColumns() {
        DataFrameTabularPrintWorker w = new DataFrameTabularPrintWorker(new StringBuilder(), 5, 4);

        assertEquals("" +
                "col1 c..2" + System.lineSeparator() +
                "---- ----" + System.lineSeparator() +
                "one  1   " + System.lineSeparator() +
                "two  2   " + System.lineSeparator() +
                "t..e 3   " + System.lineSeparator() +
                "four 4   " + System.lineSeparator() +
                "4 rows x 2 columns", w.print(df).toString());
    }
}
