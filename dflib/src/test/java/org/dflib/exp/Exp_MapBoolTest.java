package org.dflib.exp;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.builder.BoolBuilder;
import org.dflib.series.FalseSeries;
import org.dflib.series.TrueSeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.*;

public class Exp_MapBoolTest {

    @Test
    void mapBool() {
        Condition c = $col(0).mapBool(s -> s.size() % 2 == 0 ? new TrueSeries(s.size()) : new FalseSeries(s.size()));
        assertTrue(c.eval(Series.ofVal(5, 10)).isTrue());
        assertTrue(c.eval(Series.ofVal(5, 9)).isFalse());
    }

    @Test
    void mapBool_Binary() {
        Condition c = $int(0).mapBool(
                $int(1),
                (s1, s2) -> BoolBuilder.buildSeries(i -> (s1.get(i) + s2.get(i)) % 2 == 0, s1.size()));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                5, 10,
                5, 9);

        new SeriesAsserts(c.eval(df)).expectData(false, true);
    }

    @Test
    void mapBoolVal() {
        Condition c = $col(0).mapBoolVal(s -> s.toString().contains("A"));
        new SeriesAsserts(c.eval(Series.of("A", "b", "a", "aA", null))).expectData(true, false, false, true, false);
    }

    @Test
    void mapBoolVal_Nulls() {
        Condition c = $col(0).mapBoolVal(s -> s == null || s.toString().contains("A"), false);
        new SeriesAsserts(c.eval(Series.of("A", "b", "a", "aA", null))).expectData(true, false, false, true, true);
    }

    @Test
    void mapBoolVal_Binary() {
        Condition c = $int(0).mapBoolVal(
                $int(1),
                (i1, i2) -> (i1 + i2) % 2 == 0);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                5, 10,
                5, 9);

        new SeriesAsserts(c.eval(df)).expectData(false, true);
    }
}
