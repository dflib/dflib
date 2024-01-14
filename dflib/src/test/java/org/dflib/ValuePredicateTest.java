package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ValuePredicateTest {

    @Test
    public void isIn_Array() {
        Series<Integer> s = Series.of(10, 20, 30, 40).select(ValuePredicate.isIn(20, 40));
        new SeriesAsserts(s).expectData(20, 40);
    }

    @Test
    public void isIn_Iterable() {
        Series<Integer> s = Series.of(10, 20, 30, 40).select(ValuePredicate.isIn(List.of(20, 40)));
        new SeriesAsserts(s).expectData(20, 40);
    }

    @Test
    public void and() {
        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).and(ValuePredicate.isIn(10, 20));
        Series<Integer> s = Series.of(10, 20, 30, 40).select(p);
        new SeriesAsserts(s).expectData(20);
    }

    @Test
    public void or() {
        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).or(ValuePredicate.isIn(10, 20));
        Series<Integer> s = Series.of(10, 20, 30, 40).select(p);
        new SeriesAsserts(s).expectData(10, 20, 40);
    }

    @Test
    public void negate() {
        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).negate();
        Series<Integer> s = Series.of(10, 20, 30, 40).select(p);
        new SeriesAsserts(s).expectData(10, 30);
    }
}
