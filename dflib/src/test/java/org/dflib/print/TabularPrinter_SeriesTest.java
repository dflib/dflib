package org.dflib.print;

import org.dflib.Series;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TabularPrinter_SeriesTest {

    @Test
    public void print_Double() {
        TabularPrinter p = new TabularPrinter(5, 100, 10);

        assertEquals(System.lineSeparator() +
                "      1.0" + System.lineSeparator() +
                "    -1.01" + System.lineSeparator() +
                " -10000.5" + System.lineSeparator() +
                "3965001.2" + System.lineSeparator() +
                "4 elements", p.print(Series.ofDouble(1.0, -1.01, -10_000.5, 3_965_001.2)));
    }

    @Test
    public void print_Int() {
        TabularPrinter p = new TabularPrinter(5, 100, 10);

        assertEquals(System.lineSeparator() +
                "      1" + System.lineSeparator() +
                "     -1" + System.lineSeparator() +
                " -10000" + System.lineSeparator() +
                "3965001" + System.lineSeparator() +
                "4 elements", p.print(Series.ofInt(1, -1, -10_000, 3_965_001)));
    }

    @Test
    public void print_Boolean() {
        TabularPrinter p = new TabularPrinter(5, 100, 10);

        assertEquals(System.lineSeparator() +
                " true" + System.lineSeparator() +
                "false" + System.lineSeparator() +
                "2 elements", p.print(Series.ofBool(true, false)));
    }

    @Test
    public void print_Full() {
        TabularPrinter p = new TabularPrinter(5, 100, 10);

        assertEquals(System.lineSeparator() +
                "one  " + System.lineSeparator() +
                "two  " + System.lineSeparator() +
                "three" + System.lineSeparator() +
                "four " + System.lineSeparator() +
                "4 elements", p.print(Series.of("one", "two", "three", "four")));
    }

    @Test
    public void print_TruncateRows() {

        TabularPrinter p = new TabularPrinter(2, 100, 10);

        assertEquals(System.lineSeparator() +
                "one " + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "four" + System.lineSeparator() +
                "4 elements", p.print(Series.of("one", "two", "three", "four")));
    }

    @Test
    public void print_TruncateToOneRow() {
        TabularPrinter p = new TabularPrinter(1, 100, 10);

        assertEquals(System.lineSeparator() +
                "one" + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "4 elements", p.print(Series.of("one", "two", "three", "four")));
    }

    @Test
    public void print_TruncateVals() {
        TabularPrinter p = new TabularPrinter(5, 100, 4);

        assertEquals(System.lineSeparator() +
                "one " + System.lineSeparator() +
                "two " + System.lineSeparator() +
                "t..e" + System.lineSeparator() +
                "four" + System.lineSeparator() +
                "4 elements", p.print(Series.of("one", "two", "three", "four")));
    }

    @Test
    public void print_EmptyStringsOnly() {

        TabularPrinter p = new TabularPrinter(5, 100, 10);

        assertEquals(System.lineSeparator() +
                " " + System.lineSeparator() +
                " " + System.lineSeparator() +
                "2 elements", p.print(Series.of("", "")));
    }
}
