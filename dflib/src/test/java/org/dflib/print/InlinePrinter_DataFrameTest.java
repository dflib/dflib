package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InlinePrinter_DataFrameTest {

    private static final DataFrame df = DataFrame
            .byColumn("col1", "column2")
            .of(
                    Series.of("one", "two", "three", "four"),
                    Series.ofInt(1, 2, 3, 44));

    @Test
    public void print_TruncateRows() {
        InlinePrinter p = new InlinePrinter(2, 10);

        assertEquals(
                "{col1:one,column2:1},...,{col1:four,column2:44}", p.print(df));
    }

    @Test
    public void print_TruncateToOneRow() {
        InlinePrinter p = new InlinePrinter(1, 10);

        assertEquals("{col1:one,column2:1},...", p.print(df));
    }

    @Test
    public void print_EmptyColumn() {

        DataFrame df = DataFrame
                .byColumn("col1", "")
                .of(
                        Series.of("one", "two"),
                        Series.of("", ""));

        InlinePrinter p = new InlinePrinter(5, 10);

        assertEquals("{col1:one,:},{col1:two,:}", p.print(df));
    }

    @Test
    public void print_NoCols() {

        DataFrame df = DataFrame
                .byColumn()
                .of();

        InlinePrinter p = new InlinePrinter(5, 10);

        assertEquals("", p.print(df));
    }

}
