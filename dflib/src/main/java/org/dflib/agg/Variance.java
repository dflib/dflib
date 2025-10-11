package org.dflib.agg;

import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * @since 2.0.0
 */
public class Variance {

    // TODO: replace ofXSeries with array access that can be invoked from specific series (e.g. see Percentiles.ofArray(..))

    public static double ofIntSeries(IntSeries s, boolean usePopulationVariance) {
        int len = s.size();
        if (len == 0) {
            return Double.NaN;
        } else if (len == 1) {
            return usePopulationVariance ? 0. : Double.NaN;
        }

        double avg = s.avg();
        double denominator = usePopulationVariance ? len : len - 1;

        double acc = 0;
        for (int i = 0; i < len; i++) {
            final double x = s.getInt(i);
            acc += (x - avg) * (x - avg);
        }

        return acc / denominator;
    }

    public static double ofLongSeries(LongSeries s, boolean usePopulationVariance) {
        int len = s.size();
        if (len == 0) {
            return Double.NaN;
        } else if (len == 1) {
            return usePopulationVariance ? 0. : Double.NaN;
        }

        double avg = s.avg();
        double denominator = usePopulationVariance ? len : len - 1;

        double acc = 0;
        for (int i = 0; i < len; i++) {
            final double x = s.getLong(i);
            acc += (x - avg) * (x - avg);
        }

        return acc / denominator;
    }

    public static double ofFloatSeries(FloatSeries s, boolean usePopulationVariance) {
        int len = s.size();
        if (len == 0) {
            return Double.NaN;
        } else if (len == 1) {
            return usePopulationVariance ? 0. : Double.NaN;
        }

        float avg = s.avg();
        double denominator = usePopulationVariance ? len : len - 1;

        double acc = 0;
        for (int i = 0; i < len; i++) {
            float x = s.getFloat(i);
            acc += (x - avg) * (x - avg);
        }

        return acc / denominator;
    }

    public static double ofDoubleSeries(DoubleSeries s, boolean usePopulationVariance) {
        int len = s.size();
        if (len == 0) {
            return Double.NaN;
        } else if (len == 1) {
            return usePopulationVariance ? 0. : Double.NaN;
        }

        double avg = s.avg();
        double denominator = usePopulationVariance ? len : len - 1;

        double acc = 0;
        for (int i = 0; i < len; i++) {
            final double x = s.getDouble(i);
            acc += (x - avg) * (x - avg);
        }

        return acc / denominator;
    }

    public static double ofDoubles(Series<? extends Number> s, boolean usePopulationVariance) {
        return SeriesCompactor.toDoubleSeries(s).variance(usePopulationVariance);
    }

    public static BigDecimal ofBigints(Series<BigInteger> s, boolean usePopulationVariance) {

        Series<BigInteger> noNulls = SeriesCompactor.noNullsSeries(s);

        int len = noNulls.size();
        if (len == 0) {
            return null;
        } else if (len == 1) {
            return usePopulationVariance ? BigDecimal.ZERO : null;
        }

        BigDecimal avg = Average.ofBigints_NullsNotExpected(noNulls);
        MathContext mc = DecimalOps.op1Context(avg);
        BigDecimal denominator = new BigDecimal(usePopulationVariance ? len : len - 1);

        BigDecimal acc = BigDecimal.ZERO;
        for (int i = 0; i < len; i++) {

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

        int len = s.size();
        if (len == 0) {
            return null;
        } else if (len == 1) {
            return usePopulationVariance ? BigDecimal.ZERO : null;
        }

        BigDecimal avg = Average.ofDecimals(s);
        MathContext mc = DecimalOps.op1Context(avg);

        BigDecimal acc = BigDecimal.ZERO;
        int nonNullSize = len;

        for (int i = 0; i < len; i++) {
            BigDecimal x = s.get(i);
            if (x == null) {
                nonNullSize--;
                continue;
            }

            BigDecimal dev = x.subtract(avg, mc);
            BigDecimal square = dev.multiply(dev, mc);

            acc = acc.add(square, mc);
        }

        BigDecimal denominator = new BigDecimal(usePopulationVariance ? nonNullSize : nonNullSize - 1);
        return acc.divide(denominator, mc);
    }
}
