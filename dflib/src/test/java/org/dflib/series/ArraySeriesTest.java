package org.dflib.series;

import org.dflib.Series;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArraySeriesTest {

    @Test
    void nominalType() {
        ArraySeries<Object> s1 = new ArraySeries<>("a", 5, null);
        assertEquals(Object.class, s1.getNominalType());

        ArraySeries<String> s2 = new ArraySeries<>("a", "b", null);
        assertEquals(String.class, s2.getNominalType());

        ArraySeries<Integer> s3 = new ArraySeries<>(1, 2, null);
        assertEquals(Integer.class, s3.getNominalType());
    }

    @Test
    void fillNulls_PreserveNominalType() {
        Series<Object> s1 = new ArraySeries<>("a", 5, null);
        assertEquals(Object.class, s1.fillNulls(new Object()).getNominalType());

        ArraySeries<String> s2 = new ArraySeries<>("a", "b", null);
        assertEquals(String.class, s2.fillNulls("c").getNominalType());

        ArraySeries<Integer> s3 = new ArraySeries<>(1, 2, null);
        assertEquals(Integer.class, s3.fillNulls(6).getNominalType());
    }
}
