package com.nhl.dflib.print;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TabularPrinter_SeriesTest {

    private Series<String> s1;

    @BeforeEach
    public void initSeries() {
        this.s1 = Series.of("one", "two", "three", "four");
    }

    @Test
    public void testToString_Double() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(System.lineSeparator() +
                "      1.0" + System.lineSeparator() +
                "    -1.01" + System.lineSeparator() +
                " -10000.5" + System.lineSeparator() +
                "3965001.2" + System.lineSeparator() +
                "4 elements", p.toString(Series.ofDouble(1.0, -1.01, -10_000.5, 3_965_001.2)));
    }

    @Test
    public void testToString_Int() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(System.lineSeparator() +
                "      1" + System.lineSeparator() +
                "     -1" + System.lineSeparator() +
                " -10000" + System.lineSeparator() +
                "3965001" + System.lineSeparator() +
                "4 elements", p.toString(Series.ofInt(1, -1, -10_000, 3_965_001)));
    }

    @Test
    public void testToString_Boolean() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(System.lineSeparator() +
                " true" + System.lineSeparator() +
                "false" + System.lineSeparator() +
                "2 elements", p.toString(Series.ofBool(true, false)));
    }

    @Test
    public void testToString() {
        TabularPrinter p = new TabularPrinter(5, 10);

        assertEquals(System.lineSeparator() +
                "one  " + System.lineSeparator() +
                "two  " + System.lineSeparator() +
                "three" + System.lineSeparator() +
                "four " + System.lineSeparator() +
                "4 elements", p.toString(s1));
    }

    @Test
    public void testToString_TruncateRows() {

        TabularPrinter p = new TabularPrinter(2, 10);

        assertEquals(System.lineSeparator() +
                "one " + System.lineSeparator() +
                "... " + System.lineSeparator() +
                "four" + System.lineSeparator() +
                "4 elements", p.toString(s1));
    }

    @Test
    public void testToString_TruncateColumns() {
        TabularPrinter p = new TabularPrinter(5, 4);

        assertEquals(System.lineSeparator() +
                "one " + System.lineSeparator() +
                "two " + System.lineSeparator() +
                "t..e" + System.lineSeparator() +
                "four" + System.lineSeparator() +
                "4 elements", p.toString(s1));
    }
}
