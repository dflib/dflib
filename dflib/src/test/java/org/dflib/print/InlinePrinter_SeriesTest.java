package org.dflib.print;

import org.dflib.Series;
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
    public void print_Normal() {
        InlinePrinter p = new InlinePrinter(5, 10);

        assertEquals("one,two,three,four", p.print(s1));
    }

    @Test
    public void print_TruncateRows() {
        InlinePrinter p = new InlinePrinter(2, 10);
        assertEquals("one,...,four", p.print(s1));
    }

    @Test
    public void print_TruncateToOneRow() {
        InlinePrinter p = new InlinePrinter(1, 10);
        assertEquals("one,...", p.print(s1));
    }

    @Test
    public void print_TruncateColumns() {
        InlinePrinter p = new InlinePrinter(5, 4);
        assertEquals("one,two,t..e,four", p.print(s1));
    }
}
