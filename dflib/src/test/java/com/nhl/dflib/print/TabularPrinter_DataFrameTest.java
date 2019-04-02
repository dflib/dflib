package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabularPrinter_DataFrameTest {

    private DataFrame df;

    @Before
    public void initDataFrame() {
        Index columns = Index.forLabels("col1", "column2");
        this.df = DataFrame.forSequenceFoldByRow(columns,
                "one", 1,
                "two", 2,
                "three", 3,
                "four", 4);
    }

    @Test
    public void testToString() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals("" +
                "col1  column2" + System.lineSeparator() +
                "----- -------" + System.lineSeparator() +
                "one   1      " + System.lineSeparator() +
                "two   2      " + System.lineSeparator() +
                "three 3      " + System.lineSeparator() +
                "four  4      " + System.lineSeparator() +
                "4 rows x 2 columns", p.toString(df));
    }

    @Test
    public void testToString_TruncateRows() {
        TabularPrinter p = new TabularPrinter(2, 10);

        assertEquals("" +
                "col1 column2" + System.lineSeparator() +
                "---- -------" + System.lineSeparator() +
                "one  1      " + System.lineSeparator() +
                "two  2      " + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "4 rows x 2 columns", p.toString(df));
    }

    @Test
    public void testToString_TruncateColumns() {
        TabularPrinter p = new TabularPrinter(5, 4);

        assertEquals("" +
                "col1 c..2" + System.lineSeparator() +
                "---- ----" + System.lineSeparator() +
                "one  1   " + System.lineSeparator() +
                "two  2   " + System.lineSeparator() +
                "t..e 3   " + System.lineSeparator() +
                "four 4   " + System.lineSeparator() +
                "4 rows x 2 columns", p.toString(df));
    }
}
