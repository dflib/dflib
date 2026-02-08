package org.dflib.union;

import org.dflib.BooleanSeries;
import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.series.ArraySeries;

/**
 * @since 2.0.0
 */
public class SeriesUnion {

    private enum PrimitiveType {
        BOOL, INT, LONG, FLOAT, DOUBLE, NONE
    }

    /**
     * Combines multiple Series of various length into one longer Series.
     * If all Series are of the same primitive type, delegates to the specialized method for better performance.
     */
    public static <T> Series<T> of(Series<?>... series) {

        return switch (series.length) {
            case 0 -> Series.of();
            case 1 -> (Series<T>) series[0];
            default -> of(unionHeight(series), series);
        };
    }

    /**
     * Combines multiple Series of various length into one longer Series.
     */
    public static IntSeries ofInt(IntSeries... series) {

        return switch (series.length) {
            case 0 -> Series.ofInt();
            case 1 -> series[0];
            default -> ofInt(unionHeight(series), series);
        };
    }


    /**
     * Combines multiple Series of various length into one longer Series.
     */
    public static LongSeries ofLong(LongSeries... series) {

        return switch (series.length) {
            case 0 -> Series.ofLong();
            case 1 -> series[0];
            default -> ofLong(unionHeight(series), series);
        };
    }

    /**
     * Combines multiple Series of various length into one longer Series.
     */
    public static DoubleSeries ofDouble(DoubleSeries... series) {

        return switch (series.length) {
            case 0 -> Series.ofDouble();
            case 1 -> series[0];
            default -> ofDouble(unionHeight(series), series);
        };
    }

    /**
     * Combines multiple Series of various length into one longer Series.
     */
    public static FloatSeries ofFloat(FloatSeries... series) {

        return switch (series.length) {
            case 0 -> Series.ofFloat();
            case 1 -> series[0];
            default -> ofFloat(unionHeight(series), series);
        };
    }

    /**
     * Combines multiple Series of various length into one longer Series.
     */
    public static BooleanSeries ofBool(BooleanSeries... series) {

        return switch (series.length) {
            case 0 -> Series.ofBool();
            case 1 -> series[0];
            default -> ofBool(unionHeight(series), series);
        };
    }

    static <T> Series<T> of(int unionHeight, Series<?>... series) {

        PrimitiveType commonType = getCommonPrimitiveType(series);

        return switch (commonType) {
            case INT -> {
                IntSeries[] intSeries = new IntSeries[series.length];
                System.arraycopy(series, 0, intSeries, 0, series.length);
                yield (Series<T>) ofInt(unionHeight, intSeries);
            }
            case LONG -> {
                LongSeries[] longSeries = new LongSeries[series.length];
                System.arraycopy(series, 0, longSeries, 0, series.length);
                yield (Series<T>) ofLong(unionHeight, longSeries);
            }
            case DOUBLE -> {
                DoubleSeries[] doubleSeries = new DoubleSeries[series.length];
                System.arraycopy(series, 0, doubleSeries, 0, series.length);
                yield (Series<T>) ofDouble(unionHeight, doubleSeries);
            }
            case FLOAT -> {
                FloatSeries[] floatSeries = new FloatSeries[series.length];
                System.arraycopy(series, 0, floatSeries, 0, series.length);
                yield (Series<T>) ofFloat(unionHeight, floatSeries);
            }
            case BOOL -> {
                BooleanSeries[] boolSeries = new BooleanSeries[series.length];
                System.arraycopy(series, 0, boolSeries, 0, series.length);
                yield (Series<T>) ofBool(unionHeight, boolSeries);
            }
            case NONE -> {

                T[] data = (T[]) new Object[unionHeight];

                int offset = 0;
                for (Series<?> s : series) {
                    int len = s.size();
                    s.copyTo(data, 0, offset, len);
                    offset += len;
                }

                yield new ArraySeries<>(data);
            }
        };
    }

    static IntSeries ofInt(int unionHeight, IntSeries... series) {
        int[] data = new int[unionHeight];

        int offset = 0;
        for (IntSeries s : series) {
            int len = s.size();
            s.copyToInt(data, 0, offset, len);
            offset += len;
        }

        return Series.ofInt(data);
    }


    static LongSeries ofLong(int unionHeight, LongSeries... series) {

        long[] data = new long[unionHeight];

        int offset = 0;
        for (LongSeries s : series) {
            int len = s.size();
            s.copyToLong(data, 0, offset, len);
            offset += len;
        }

        return Series.ofLong(data);
    }


    static DoubleSeries ofDouble(int unionHeight, DoubleSeries... series) {

        double[] data = new double[unionHeight];

        int offset = 0;
        for (DoubleSeries s : series) {
            int len = s.size();
            s.copyToDouble(data, 0, offset, len);
            offset += len;
        }

        return Series.ofDouble(data);
    }

    static FloatSeries ofFloat(int unionHeight, FloatSeries... series) {

        float[] data = new float[unionHeight];

        int offset = 0;
        for (FloatSeries s : series) {
            int len = s.size();
            s.copyToFloat(data, 0, offset, len);
            offset += len;
        }

        return Series.ofFloat(data);
    }

    static BooleanSeries ofBool(int unionHeight, BooleanSeries... series) {

        boolean[] data = new boolean[unionHeight];

        int offset = 0;
        for (BooleanSeries s : series) {
            int len = s.size();
            s.copyToBool(data, 0, offset, len);
            offset += len;
        }

        return Series.ofBool(data);
    }

    private static PrimitiveType getCommonPrimitiveType(Series<?>[] series) {
        if (series.length == 0) {
            return PrimitiveType.NONE;
        }

        PrimitiveType firstType = getPrimitiveType(series[0]);
        if (firstType == PrimitiveType.NONE) {
            return PrimitiveType.NONE;
        }

        for (int i = 1; i < series.length; i++) {
            PrimitiveType type = getPrimitiveType(series[i]);
            if (type != firstType) {
                return PrimitiveType.NONE;
            }
        }

        return firstType;
    }

    private static PrimitiveType getPrimitiveType(Series<?> series) {
        if (series instanceof IntSeries) {
            return PrimitiveType.INT;
        } else if (series instanceof LongSeries) {
            return PrimitiveType.LONG;
        } else if (series instanceof DoubleSeries) {
            return PrimitiveType.DOUBLE;
        } else if (series instanceof FloatSeries) {
            return PrimitiveType.FLOAT;
        } else if (series instanceof BooleanSeries) {
            return PrimitiveType.BOOL;
        }
        return PrimitiveType.NONE;
    }

    private static int unionHeight(Series<?>[] series) {
        int h = 0;
        for (Series<?> s : series) {
            h += s.size();
        }

        return h;
    }
}
