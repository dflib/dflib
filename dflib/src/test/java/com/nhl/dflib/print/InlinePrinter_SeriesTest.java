package com.nhl.dflib.print;

import com.nhl.dflib.Series;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InlinePrinter_SeriesTest {

    private Series<String> s1;

    @BeforeEach
    public void initSeries() {
        this.s1 = Series.of("one", "two", "three", "four");
    }

    @Test
    public void testToString() {
        InlinePrinter p = new InlinePrinter(5, 10);

        assertEquals("one,two,three,four", p.toString(s1));
    }

    @Test
    public void testToString_TruncateRows() {
        InlinePrinter p = new InlinePrinter(2, 10);
        assertEquals("one,...,four", p.toString(s1));
    }

    @Test
    public void testToString_TruncateColumns() {
        InlinePrinter p = new InlinePrinter(5, 4);
        assertEquals("one,two,t..e,four", p.toString(s1));
    }
}
