package org.dflib.explode;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.builder.ValueAccum;
import org.dflib.builder.IntAccum;
import org.dflib.builder.ObjectAccum;

import java.util.Iterator;

/**
 * @since 1.0.0-M19
 */
public class Exploder {

    private final Series<?> exploded;
    private final IntSeries stretchCounts;

    private Exploder(Series<?> exploded, IntSeries stretchCounts) {
        this.exploded = exploded;
        this.stretchCounts = stretchCounts;
    }

    public Series<?> getExploded() {
        return exploded;
    }

    public IntSeries getStretchCounts() {
        return stretchCounts;
    }

    public static Exploder explode(Series<?> s) {

        int h = s.size();
        ValueAccum<Object> explodedAccum = new ObjectAccum<>(h);
        IntAccum indexAccum = new IntAccum(h);

        for (int i = 0; i < h; i++) {
            Object v = s.get(i);
            if (v == null) {
                explodedAccum.push(null);
                indexAccum.push(1);
            } else if (v instanceof Iterable) {
                explodeIterable(explodedAccum, indexAccum, (Iterable<?>) v);
            } else if (v.getClass().isArray()) {
                explodeArray(explodedAccum, indexAccum, v);
            }
            // scalar
            else {
                explodedAccum.push(v);
                indexAccum.push(1);
            }
        }

        return new Exploder(explodedAccum.toSeries(), indexAccum.toSeries());
    }

    private static void explodeIterable(
            ValueAccum<Object> explodedAccum,
            IntAccum indexAccum,
            Iterable<?> iterable) {

        // empty iterable should generate a single null row
        Iterator<?> it = iterable.iterator();

        if (!it.hasNext()) {
            explodedAccum.push(null);
            indexAccum.push(1);
        } else {
            int c = 0;
            while (it.hasNext()) {
                explodedAccum.push(it.next());
                c++;
            }

            indexAccum.push(c);
        }
    }

    private static void explodeArray(
            ValueAccum<Object> explodedAccum,
            IntAccum indexAccum,
            Object array) {

        if (array instanceof Object[]) {
            Object[] a = (Object[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(1);
            } else {
                for (Object sv : a) {
                    explodedAccum.push(sv);
                }

                indexAccum.push(a.length);
            }
        } else if (array instanceof int[]) {
            int[] a = (int[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(1);
            } else {
                for (int sv : a) {
                    explodedAccum.push(sv);
                }
                indexAccum.push(a.length);
            }
        } else if (array instanceof double[]) {
            double[] a = (double[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(1);
            } else {
                for (double sv : a) {
                    explodedAccum.push(sv);
                }
                indexAccum.push(a.length);
            }
        } else if (array instanceof long[]) {
            long[] a = (long[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(1);
            } else {
                for (long sv : a) {
                    explodedAccum.push(sv);
                }

                indexAccum.push(a.length);
            }
        } else if (array instanceof boolean[]) {
            boolean[] a = (boolean[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(1);
            } else {
                for (boolean sv : a) {
                    explodedAccum.push(sv);
                }

                indexAccum.push(a.length);
            }
        } else if (array instanceof byte[]) {
            byte[] a = (byte[]) array;

            // empty array should generate a single null row
            if (a.length == 0) {
                explodedAccum.push(null);
                indexAccum.push(1);
            } else {
                for (byte sv : a) {
                    explodedAccum.push(sv);
                }
                indexAccum.push(a.length);
            }
        }
        // TODO: short[], float[]?
        else {
            throw new IllegalArgumentException("Unrecognized array type: " + array.getClass().getName());
        }
    }
}
