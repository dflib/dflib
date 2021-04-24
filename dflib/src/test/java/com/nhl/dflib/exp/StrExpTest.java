package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrExpTest {

    @Test
    public void testReadColumn() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "2", "3",
                "4", "5", "6");

        Series<String> s = $str("b").eval(df);
        new SeriesAsserts(s).expectData("2", "5");
    }

    @Test
    public void testNamed() {
        Exp<String> e = $str("b");
        assertEquals("b", e.getName());
        assertEquals("c", e.as("c").getName());
    }

    @Test
    public void testConcat() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "2", "3",
                "4", "5", "6");

        Series<String> s = $str("b").concat($str("a")).eval(df);
        new SeriesAsserts(s).expectData("21", "54");
    }
}
