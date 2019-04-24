package com.nhl.dflib.print;

import com.nhl.dflib.Series;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TabularPrinter_SeriesTest {

    private Series<String> s1;

    @Before
    public void initSeries() {
        this.s1 = Series.forData("one", "two", "three", "four");
    }

    @Test
    public void testToString() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(System.lineSeparator() +
                "one" + System.lineSeparator() +
                "two" + System.lineSeparator() +
                "three" + System.lineSeparator() +
                "four" + System.lineSeparator() +
                "4 rows", p.toString(s1));
    }

    @Test
    public void testToString_TruncateRows() {

        TabularPrinter p = new TabularPrinter(2, 10);

        assertEquals(System.lineSeparator() +
                "one" + System.lineSeparator() +
                "two" + System.lineSeparator() +
                "..." + System.lineSeparator() +
                "4 rows", p.toString(s1));
    }

    @Test
    public void testToString_TruncateColumns() {
        TabularPrinter p = new TabularPrinter(5, 4);

        assertEquals(System.lineSeparator() +
                "one" + System.lineSeparator() +
                "two" + System.lineSeparator() +
                "t..e" + System.lineSeparator() +
                "four" + System.lineSeparator() +
                "4 rows", p.toString(s1));
    }
}
