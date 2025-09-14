package org.dflib.exp.str;

import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubstrFromExpTest extends BaseExpTest {

    @Test
    public void toQL() {
        StrExp exp = $col("a").substr(1);
        assertEquals("substr(a,1)", exp.toQL());
    }

    @Test
    public void zero() {
        StrExp exp = $str(0).substr(0);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "a", "ab", "abc", "abcd");
    }

    @Test
    public void positive() {
        StrExp exp = $str(0).substr(2);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "", "", "c", "cd");
    }

    @Test
    public void negative() {
        StrExp exp = $str(0).substr(-2);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "", "", "bc", "cd");
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $col("a").substr(2),
                $col("a").substr(2),
                $col("b").substr(2));

        assertExpEquals(
                $col("a").substr(2),
                $col("a").substr(2),
                $col("a").substr(3));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $col("a").substr(2),
                $col("a").substr(2),
                $col("b").substr(2));

        assertExpHashCode(
                $col("a").substr(2),
                $col("a").substr(2),
                $col("a").substr(3));
    }
}
