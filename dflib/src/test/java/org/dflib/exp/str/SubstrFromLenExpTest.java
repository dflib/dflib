package org.dflib.exp.str;

import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubstrFromLenExpTest extends BaseExpTest {

    @Test
    public void toQL() {
        StrExp exp = $col("a").substr(1, 2);
        assertEquals("substr(a,1,2)", exp.toQL());
    }

    @Test
    public void len() {
        StrExp exp = $str(0).substr(2, 1);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "", "", "c", "c");
    }

    @Test
    public void negative_Len() {
        StrExp exp = $str(0).substr(-2, 1);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "", "", "b", "c");
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $col("a").substr(2, 1),
                $col("a").substr(2, 1),
                $col("b").substr(2, 1));

        assertExpEquals(
                $col("a").substr(2, 1),
                $col("a").substr(2, 1),
                $col("a").substr(3, 1));

        assertExpEquals(
                $col("a").substr(2, 1),
                $col("a").substr(2, 1),
                $col("a").substr(2, 2));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $col("a").substr(2, 1),
                $col("a").substr(2, 1),
                $col("b").substr(2, 1));

        assertExpHashCode(
                $col("a").substr(2, 1),
                $col("a").substr(2, 1),
                $col("a").substr(3, 1));

        assertExpHashCode(
                $col("a").substr(2, 1),
                $col("a").substr(2, 1),
                $col("a").substr(2, 2));
    }
}
