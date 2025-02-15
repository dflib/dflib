package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TabularPrinter_DataFrameTest {

    private static final String LS = System.lineSeparator();

    private static final DataFrame df = DataFrame
            .byColumn("col1", "column2")
            .of(
                    Series.of("one", "two", "three", "four"),
                    Series.ofInt(1, 2, 3, 44));

    @Test
    public void print_Normal() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(LS +
                "col1  column2" + LS +
                "----- -------" + LS +
                "one         1" + LS +
                "two         2" + LS +
                "three       3" + LS +
                "four       44" + LS +
                "4 rows x 2 columns", p.print(df));
    }

    @Test
    public void print_Name() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(LS +
                "col1  column2" + LS +
                "----- -------" + LS +
                "one         1" + LS +
                "two         2" + LS +
                "three       3" + LS +
                "four       44" + LS +
                "[df0] 4 rows x 2 columns", p.print(df.as("df0")));
    }

    @Test
    public void print_TruncateRows() {
        TabularPrinter p = new TabularPrinter(2, 10);

        assertEquals(LS +
                "col1 column2" + LS +
                "---- -------" + LS +
                "one        1" + LS +
                "..." + LS +
                "four      44" + LS +
                "4 rows x 2 columns", p.print(df));
    }

    @Test
    public void print_TruncateColumns() {
        TabularPrinter p = new TabularPrinter(5, 4);

        assertEquals(LS +
                "col1 c..2" + LS +
                "---- ----" + LS +
                "one     1" + LS +
                "two     2" + LS +
                "t..e    3" + LS +
                "four   44" + LS +
                "4 rows x 2 columns", p.print(df));
    }

    @Test
    public void emptyColumn() {

        DataFrame df = DataFrame
                .byColumn("col1", "")
                .of(
                        Series.of("one", "two"),
                        Series.of("", ""));

        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(LS +
                "col1  " + LS +
                "---- -" + LS +
                "one   " + LS +
                "two   " + LS +
                "[df0] 2 rows x 2 columns", p.print(df.as("df0")));
    }

    @Test
    public void nullIndexLabel() {

        DataFrame df = DataFrame
                .byColumn("col1", null)
                .of(
                        Series.of("one", "two"),
                        Series.of("", ""));

        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(LS +
                "col1 null" + LS +
                "---- ----" + LS +
                "one      " + LS +
                "two      " + LS +
                "[df0] 2 rows x 2 columns", p.print(df.as("df0")));
    }
}
