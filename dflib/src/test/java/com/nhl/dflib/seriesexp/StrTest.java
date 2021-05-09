package com.nhl.dflib.seriesexp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class StrTest {

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
        SeriesExp<String> e = $str("b");
        assertEquals("b", e.getName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getName(mock(DataFrame.class)));
    }

    @Test
    public void testConcat() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "2", "3",
                4, "5", "6",
                7, null, null,
                8, "", "9");

        Series<String> s1 = concat($str("b"), $int("a")).eval(df);
        new SeriesAsserts(s1).expectData("21", "54", null, "8");

        Series<String> s2 = concat("_", $str("b"), "]").eval(df);
        new SeriesAsserts(s2).expectData("_2]", "_5]", null, "_]");
    }
}
