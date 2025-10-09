package org.dflib.agg;

import org.dflib.Series;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * @since 2.0.0
 */
public class Variance {

    public static double ofDoubles(Series<? extends Number> s, boolean usePopulationVariance) {
        return SeriesCompactor.toDoubleSeries(s).variance(usePopulationVariance);
    }

    public static BigDecimal ofBigints(Series<BigInteger> s, boolean usePopulationVariance) {

        Series<BigInteger> noNulls = SeriesCompactor.noNullsSeries(s);

        int size = noNulls.size();
        BigDecimal avg = Average.ofBigints_NullsNotExpected(noNulls);
        MathContext mc = DecimalOps.op1Context(avg);
        BigDecimal denominator = new BigDecimal(usePopulationVariance ? size : size - 1);

        BigDecimal acc = BigDecimal.ZERO;
        for (int i = 0; i < size; i++) {

            // TODO: I suspect "avg(..)" above already does BigInteger -> BigDecimal conversion, so maybe worth
            //  implementing this whole thing as "castToDecimal().variance()" ?
            BigDecimal x = new BigDecimal(s.get(i));

            BigDecimal dev = x.subtract(avg, mc);
            BigDecimal square = dev.multiply(dev, mc);

            acc = acc.add(square, mc);
        }

        return acc.divide(denominator, mc);
    }

    public static BigDecimal ofDecimals(Series<BigDecimal> s, boolean usePopulationVariance) {

        Series<BigDecimal> noNulls = SeriesCompactor.noNullsSeries(s);
        int size = noNulls.size();

        BigDecimal avg = Average.ofDecimals_NullsNotExpected(noNulls);
        MathContext mc = DecimalOps.op1Context(avg);
        BigDecimal denominator = new BigDecimal(usePopulationVariance ? size : size - 1);

        BigDecimal acc = BigDecimal.ZERO;
        for (int i = 0; i < size; i++) {
            BigDecimal x = s.get(i);

            BigDecimal dev = x.subtract(avg, mc);
            BigDecimal square = dev.multiply(dev, mc);

            acc = acc.add(square, mc);
        }

        return acc.divide(denominator, mc);
    }
}
