package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.dflib.Exp.$bigint;
import static org.dflib.Exp.$bigintVal;

public class BigintValTest {

    @Test
    void add() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("20100287"),
                new BigInteger("45"));

        Series<?> s = $bigintVal(new BigInteger("333")).add($bigint("a")).eval(df);

        new SeriesAsserts(s).expectData(
                new BigInteger("20100620"),
                new BigInteger("378"));
    }

    @Test
    void add_Str() {
        DataFrame df = DataFrame.foldByRow("a").of(
                new BigInteger("20100287"),
                new BigInteger("45"));

        Series<?> s = $bigintVal("333").add($bigint("a")).eval(df);

        new SeriesAsserts(s).expectData(
                new BigInteger("20100620"),
                new BigInteger("378"));
    }
}
