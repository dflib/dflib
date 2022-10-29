package com.nhl.dflib.explode;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.IntAccumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.util.Iterator;

/**
 * @since 0.16
 */
public class Exploder {

    public static DataFrame explode(DataFrame df, int columnPos) {
        return df.height() > 0 ? new Exploder(columnPos, df).explode() : df;
    }

    private final int columnPos;
    private final DataFrame srcDF;
    private final Accumulator<Object> explodedAccum;
    private final IntAccumulator indexAccum;

    protected Exploder(int columnPos, DataFrame srcDF) {
        this.columnPos = columnPos;
        this.srcDF = srcDF;
        this.explodedAccum = new ObjectAccumulator<>(srcDF.height());
        this.indexAccum = new IntAccumulator(srcDF.height());
    }

    public DataFrame explode() {
        explodeColumnAndBuildIndex();
        return reassembleExploded();
    }

    private DataFrame reassembleExploded() {
        IntSeries explodeIndex = indexAccum.toSeries();
        int w = srcDF.width();
        Series[] explodedColumns = new Series[w];
        for (int i = 0; i < w; i++) {
            explodedColumns[i] = i == columnPos
                    ? explodedAccum.toSeries()
                    : srcDF.getColumn(i).select(explodeIndex);
        }

        return DataFrame.newFrame(srcDF.getColumnsIndex()).columns(explodedColumns);
    }

    private void explodeColumnAndBuildIndex() {
        Series<?> toExplode = srcDF.getColumn(columnPos);
        int h = toExplode.size();
        for (int i = 0; i < h; i++) {

            Object v = toExplode.get(i);
            if (v == null) {
                explodedAccum.add(null);
                indexAccum.add(i);
            } else if (v instanceof Iterable) {

                // empty iterable should generate a single null row
                Iterator<?> it = ((Iterable) v).iterator();

                if (!it.hasNext()) {
                    explodedAccum.add(null);
                    indexAccum.add(i);
                } else {
                    while (it.hasNext()) {
                        explodedAccum.add(it.next());
                        indexAccum.add(i);
                    }
                }

            } else if (v.getClass().isArray()) {
                explodeArray(v, i);
            }
            // scalar
            else {
                explodedAccum.add(v);
                indexAccum.add(i);
            }
        }
    }

    private void explodeArray(Object array, int i) {

        if (array instanceof Object[]) {
            Object[] a = (Object[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.add(null);
                indexAccum.add(i);
            } else {
                for (Object sv : a) {
                    explodedAccum.add(sv);
                    indexAccum.add(i);
                }
            }
        } else if (array instanceof int[]) {
            int[] a = (int[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.add(null);
                indexAccum.add(i);
            } else {
                for (int sv : a) {
                    explodedAccum.add(sv);
                    indexAccum.add(i);
                }
            }
        } else if (array instanceof double[]) {
            double[] a = (double[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.add(null);
                indexAccum.add(i);
            } else {
                for (double sv : a) {
                    explodedAccum.add(sv);
                    indexAccum.add(i);
                }
            }
        } else if (array instanceof long[]) {
            long[] a = (long[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.add(null);
                indexAccum.add(i);
            } else {
                for (long sv : a) {
                    explodedAccum.add(sv);
                    indexAccum.add(i);
                }
            }
        } else if (array instanceof boolean[]) {
            boolean[] a = (boolean[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.add(null);
                indexAccum.add(i);
            } else {
                for (boolean sv : a) {
                    explodedAccum.add(sv);
                    indexAccum.add(i);
                }
            }
        }
        else if (array instanceof byte[]) {
            byte[] a = (byte[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.add(null);
                indexAccum.add(i);
            } else {
                for (byte sv : a) {
                    explodedAccum.add(sv);
                    indexAccum.add(i);
                }
            }
        }
        // TODO: short[], float[]?
        else {
            throw new IllegalArgumentException("Unrecognized array type: " + array.getClass().getName());
        }
    }
}
