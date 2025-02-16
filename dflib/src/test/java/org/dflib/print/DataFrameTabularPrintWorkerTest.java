package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFrameTabularPrintWorkerTest {

    private DataFrame df;

    @BeforeEach
    public void initDataFrameParts() {
        this.df = DataFrame.byColumn("col1", "column2").of(
                Series.of("one", "two", "three", "four"),
                Series.ofInt(1, 2, 3, 4));
    }

    @Test
    public void appendFixedWidth1() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameTabularPrintWorker(out, 3, 20).appendFixedWidth("a", 3, CellFormatter.rightPad(3));
        assertEquals("a  ", out.toString());
    }

    @Test
    public void appendFixedWidth2() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameTabularPrintWorker(out, 3, 20).appendFixedWidth("a", 2, CellFormatter.rightPad(2));
        assertEquals("a ", out.toString());
    }

    @Test
    public void appendFixedWidth3() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameTabularPrintWorker(out, 3, 20).appendFixedWidth("a", 1, CellFormatter.rightPad(1));
        assertEquals("a", out.toString());
    }

    @Test
    public void appendFixedWidth4() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameTabularPrintWorker(out, 3, 20).appendFixedWidth("abc", 2, CellFormatter.rightPad(2));
        assertEquals("..", out.toString());
    }


    @Test
    public void print_Full() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameTabularPrintWorker(out, 5, 10).print(df);
        assertEquals(System.lineSeparator() +
                "col1  column2" + System.lineSeparator() +
                "----- -------" + System.lineSeparator() +
                "one         1" + System.lineSeparator() +
                "two         2" + System.lineSeparator() +
                "three       3" + System.lineSeparator() +
                "four        4" + System.lineSeparator() +
                "4 rows x 2 columns", out.toString());
    }

    @Test
    public void print_TruncateRows() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameTabularPrintWorker(out, 2, 10).print(df);
        assertEquals(System.lineSeparator() +
                "col1 column2" + System.lineSeparator() +
                "---- -------" + System.lineSeparator() +
                "one        1" + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "four       4" + System.lineSeparator() +
                "4 rows x 2 columns", out.toString());
    }

    @Test
    public void print_TruncateColumns() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameTabularPrintWorker(out, 5, 4).print(df);

        assertEquals(System.lineSeparator() +
                "col1 c..2" + System.lineSeparator() +
                "---- ----" + System.lineSeparator() +
                "one     1" + System.lineSeparator() +
                "two     2" + System.lineSeparator() +
                "t..e    3" + System.lineSeparator() +
                "four    4" + System.lineSeparator() +
                "4 rows x 2 columns", out.toString());
    }
}
