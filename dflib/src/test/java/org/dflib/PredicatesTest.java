package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

public class PredicatesTest {

    @Test
    public void isIn_Array() {
        Series<Integer> s = Series.of(10, 20, 30, 40).select(Predicates.isIn(20, 40));
        new SeriesAsserts(s).expectData(20, 40);
    }

    @Test
    public void isIn_Iterable() {
        Series<Integer> s = Series.of(10, 20, 30, 40).select(Predicates.isIn(List.of(20, 40)));
        new SeriesAsserts(s).expectData(20, 40);
    }

    @Test
    public void and() {
        Predicate<Integer> p = Predicates.isIn(20, 40).and(Predicates.isIn(10, 20));
        Series<Integer> s = Series.of(10, 20, 30, 40).select(p);
        new SeriesAsserts(s).expectData(20);
    }

    @Test
    public void or() {
        Predicate<Integer> p = Predicates.isIn(20, 40).or(Predicates.isIn(10, 20));
        Series<Integer> s = Series.of(10, 20, 30, 40).select(p);
        new SeriesAsserts(s).expectData(10, 20, 40);
    }

    @Test
    public void negate() {
        Predicate<Integer> p = Predicates.isIn(20, 40).negate();
        Series<Integer> s = Series.of(10, 20, 30, 40).select(p);
        new SeriesAsserts(s).expectData(10, 30);
    }
}
