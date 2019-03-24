package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabularPrinterWorkerTest {

    private DataFrame df;

    @Before
    public void initDataFrameParts() {

        Index columns = Index.withNames("col1", "column2");
        this.df = DataFrame.fromRows(columns,
                DataFrame.row("one", 1),
                DataFrame.row("two", 2),
                DataFrame.row("three", 3),
                DataFrame.row("four", 4));
    }

    @Test
    public void testAppendFixedWidth() {
        assertEquals("a  ", new TabularPrinterWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 3).toString());
        assertEquals("a ", new TabularPrinterWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 2).toString());
        assertEquals("a", new TabularPrinterWorker(new StringBuilder(), 3, 20).appendFixedWidth("a", 1).toString());
        assertEquals("..", new TabularPrinterWorker(new StringBuilder(), 3, 20).appendFixedWidth("abc", 2).toString());
    }

    @Test
    public void testPrint_Full() {
        TabularPrinterWorker w = new TabularPrinterWorker(new StringBuilder(), 5, 10);

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
        TabularPrinterWorker w = new TabularPrinterWorker(new StringBuilder(), 2, 10);

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
        TabularPrinterWorker w = new TabularPrinterWorker(new StringBuilder(), 5, 4);

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
