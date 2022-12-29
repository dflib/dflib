package com.nhl.dflib.explode;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.builder.ValueAccum;
import com.nhl.dflib.builder.IntAccum;
import com.nhl.dflib.builder.ObjectAccum;

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
    private final ValueAccum<Object> explodedAccum;
    private final IntAccum indexAccum;

    protected Exploder(int columnPos, DataFrame srcDF) {
        this.columnPos = columnPos;
        this.srcDF = srcDF;
        this.explodedAccum = new ObjectAccum<>(srcDF.height());
        this.indexAccum = new IntAccum(srcDF.height());
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

        return DataFrame.byColumn(srcDF.getColumnsIndex()).array(explodedColumns);
    }

    private void explodeColumnAndBuildIndex() {
        Series<?> toExplode = srcDF.getColumn(columnPos);
        int h = toExplode.size();
        for (int i = 0; i < h; i++) {

            Object v = toExplode.get(i);
            if (v == null) {
                explodedAccum.push(null);
                indexAccum.push(i);
            } else if (v instanceof Iterable) {

                // empty iterable should generate a single null row
                Iterator<?> it = ((Iterable) v).iterator();

                if (!it.hasNext()) {
                    explodedAccum.push(null);
                    indexAccum.push(i);
                } else {
                    while (it.hasNext()) {
                        explodedAccum.push(it.next());
                        indexAccum.push(i);
                    }
                }

            } else if (v.getClass().isArray()) {
                explodeArray(v, i);
            }
            // scalar
            else {
                explodedAccum.push(v);
                indexAccum.push(i);
            }
        }
    }

    private void explodeArray(Object array, int i) {

        if (array instanceof Object[]) {
            Object[] a = (Object[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(i);
            } else {
                for (Object sv : a) {
                    explodedAccum.push(sv);
                    indexAccum.push(i);
                }
            }
        } else if (array instanceof int[]) {
            int[] a = (int[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(i);
            } else {
                for (int sv : a) {
                    explodedAccum.push(sv);
                    indexAccum.push(i);
                }
            }
        } else if (array instanceof double[]) {
            double[] a = (double[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(i);
            } else {
                for (double sv : a) {
                    explodedAccum.push(sv);
                    indexAccum.push(i);
                }
            }
        } else if (array instanceof long[]) {
            long[] a = (long[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(i);
            } else {
                for (long sv : a) {
                    explodedAccum.push(sv);
                    indexAccum.push(i);
                }
            }
        } else if (array instanceof boolean[]) {
            boolean[] a = (boolean[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(i);
            } else {
                for (boolean sv : a) {
                    explodedAccum.push(sv);
                    indexAccum.push(i);
                }
            }
        }
        else if (array instanceof byte[]) {
            byte[] a = (byte[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(i);
            } else {
                for (byte sv : a) {
                    explodedAccum.push(sv);
                    indexAccum.push(i);
                }
            }
        }
        // TODO: short[], float[]?
        else {
            throw new IllegalArgumentException("Unrecognized array type: " + array.getClass().getName());
        }
    }
}
