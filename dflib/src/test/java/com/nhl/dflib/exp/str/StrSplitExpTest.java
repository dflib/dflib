package com.nhl.dflib.exp.str;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.DataFrameAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrSplitExpTest {

    @Test
    public void toQL() {
        Exp<String[]> exp = $str("a").split(' ');
        assertEquals("split(a)", exp.toQL());
    }

    @Test
    public void splitOnChar_Eval_DataFrame() {
        Exp<String[]> exp = $str("b").split(' ');

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "ab cd", "3",
                "4", " ef g ", "6",
                "4", "no_space", "6",
                "7", null, "9");

        new SeriesAsserts(exp.eval(df)).expectData(
                new String[]{"ab", "cd"},
                new String[]{"", "ef", "g"},
                new String[]{"no_space"}, null);
    }

    @Test
    public void splitOnChar_Range_DataFrame() {
        Exp<String[]> exp = $str("b").split(' ');

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "ab cd",
                        4, " ef g ",
                        5, "no_space",
                        8, null)
                .cols("b1", "b2").mapArrays(exp)
                .colsExcept("b").select();

        new DataFrameAsserts(df, "a", "b1", "b2")
                .expectHeight(4)
                .expectRow(0, 1, "ab", "cd")
                .expectRow(1, 4, "", "ef")
                .expectRow(2, 5, "no_space", null)
                .expectRow(3, 8, null, null);
    }

    @Test
    public void splitOnChar_Eval_Series() {
        Exp<String[]> exp = $str(0).split(' ');

        Series<String> s = Series.of("ab cd", " ef g ", null);
        new SeriesAsserts(exp.eval(s)).expectData(
                new String[]{"ab", "cd"},
                new String[]{"", "ef", "g"}, null);
    }

    @Test
    public void splitOnChar_Eval_Series_Limit() {
        Exp<String[]> exp = $str(0).split(' ', 2);

        Series<String> s = Series.of("ab cd", " ef g ", null);
        new SeriesAsserts(exp.eval(s)).expectData(
                new String[]{"ab", "cd"},
                new String[]{"", "ef g "}, null);
    }

    @Test
    public void splitOnChar_Eval_Series_NegativeLimit() {
        Exp<String[]> exp = $str(0).split(' ', -1);

        Series<String> s = Series.of("ab cd", " ef g ", null);
        new SeriesAsserts(exp.eval(s)).expectData(
                new String[]{"ab", "cd"},
                new String[]{"", "ef", "g", ""}, null);
    }

    @Test
    public void splitRegex_Eval() {
        Exp<String[]> exp = $str(0).split("\\'_");

        Series<String> s = Series.of("ab'_cd", "'_ef g '_", null);
        new SeriesAsserts(exp.eval(s)).expectData(
                new String[]{"ab", "cd"},
                new String[]{"", "ef g "},
                null);
    }

    @Test
    public void splitRegexWithLimit_Eval() {
        Exp<String[]> exp = $str(0).split("\\'_", 2);

        Series<String> s = Series.of("ab'_cd", "'_ef g '_", "AB", null);
        new SeriesAsserts(exp.eval(s)).expectData(
                new String[]{"ab", "cd"},
                new String[]{"", "ef g '_"},
                new String[]{"AB"},
                null);
    }

    @Test
    public void splitRegexWithNegativeLimit_Eval() {
        Exp<String[]> exp = $str(0).split("\\'_", -2);

        Series<String> s = Series.of("ab'_cd", "'_ef g '_", "AB", null);
        new SeriesAsserts(exp.eval(s)).expectData(
                new String[]{"ab", "cd"},
                new String[]{"", "ef g ", ""},
                new String[]{"AB"},
                null);
    }
}
