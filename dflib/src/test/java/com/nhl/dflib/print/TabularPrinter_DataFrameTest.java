package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabularPrinter_DataFrameTest {

    private DataFrame df;

    @Before
    public void initDataFrame() {
        Index columns = Index.forLabels("col1", "column2");
        this.df = DataFrame.forColumns(columns,
                Series.forData("one", "two", "three", "four"),
                IntSeries.forInts(1, 2, 3, 44));
    }

    @Test
    public void testToString() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(System.lineSeparator() +
                "col1  column2" + System.lineSeparator() +
                "----- -------" + System.lineSeparator() +
                "one         1" + System.lineSeparator() +
                "two         2" + System.lineSeparator() +
                "three       3" + System.lineSeparator() +
                "four       44" + System.lineSeparator() +
                "4 rows x 2 columns", p.toString(df));
    }

    @Test
    public void testToString_TruncateRows() {
        TabularPrinter p = new TabularPrinter(2, 10);

        assertEquals(System.lineSeparator() +
                "col1 column2" + System.lineSeparator() +
                "---- -------" + System.lineSeparator() +
                "one        1" + System.lineSeparator() +
                "two        2" + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "4 rows x 2 columns", p.toString(df));
    }

    @Test
    public void testToString_TruncateColumns() {
        TabularPrinter p = new TabularPrinter(5, 4);

        assertEquals(System.lineSeparator() +
                "col1 c..2" + System.lineSeparator() +
                "---- ----" + System.lineSeparator() +
                "one     1" + System.lineSeparator() +
                "two     2" + System.lineSeparator() +
                "t..e    3" + System.lineSeparator() +
                "four   44" + System.lineSeparator() +
                "4 rows x 2 columns", p.toString(df));
    }
}
