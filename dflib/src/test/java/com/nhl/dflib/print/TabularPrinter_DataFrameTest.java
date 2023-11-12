package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TabularPrinter_DataFrameTest {

    private DataFrame df;

    @BeforeEach
    public void initDataFrame() {
        this.df = DataFrame
                .newFrame("col1", "column2")
                .columns(Series.of("one", "two", "three", "four"), Series.ofInt(1, 2, 3, 44));
    }

    @Test
    public void toString_Normal() {
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
    public void toString_TruncateRows() {
        TabularPrinter p = new TabularPrinter(2, 10);

        assertEquals(System.lineSeparator() +
                "col1 column2" + System.lineSeparator() +
                "---- -------" + System.lineSeparator() +
                "one        1" + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "four      44" + System.lineSeparator() +
                "4 rows x 2 columns", p.toString(df));
    }

    @Test
    public void toString_TruncateColumns() {
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
