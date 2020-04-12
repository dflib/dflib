package com.nhl.dflib.unit;

import com.nhl.dflib.Series;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SeriesAsserts {

    private Object[] data;

    public SeriesAsserts(Series<?> series) {
        assertNotNull(series, "Series is null");

        this.data = new Object[series.size()];
        series.copyTo(data, 0, 0, series.size());
    }

    public SeriesAsserts expectData(Object... expectedValues) {

        assertEquals(expectedValues.length, data.length, "Unexpected Series length");

        for (int i = 0; i < expectedValues.length; i++) {

            Object a = data[i];
            Object e = expectedValues[i];

            if (e == null) {
                assertNull(a, "Unexpected value at " + i);
            } else if (e.getClass().isArray()) {
                assertTrue( a.getClass().isArray(), "Was expecting array at " + i);
                expectArrayRow(i, e, a);
            } else {
                assertEquals( e, a, "Unexpected value at " + i);
            }
        }

        return this;
    }

    private void expectArrayRow(int i, Object expected, Object actual) {

        String eArrayClass = expected.getClass().getSimpleName();
        String aArrayClass = actual.getClass().getSimpleName();
        assertEquals("Unexpected array type at '" + i, eArrayClass, aArrayClass);

        // remove []
        String type = eArrayClass.substring(0, eArrayClass.length() - 2);
        switch (type) {
            case "char":
                assertArrayEquals((char[]) expected, (char[]) actual, "Unexpected value at " + i);
                break;
            case "long":
                assertArrayEquals((long[]) expected, (long[]) actual, "Unexpected value at " + i);
                break;
            case "int":
                assertArrayEquals((int[]) expected, (int[]) actual, "Unexpected value at " + i);
                break;
            case "byte":
                assertArrayEquals((byte[]) expected, (byte[]) actual, "Unexpected value at " + i);
                break;
            case "double":
                assertArrayEquals((double[]) expected, (double[]) actual, 0.00001, "Unexpected value at " + i);
                break;
            case "float":
                assertArrayEquals((float[]) expected, (float[]) actual, 0.00001f, "Unexpected value at " + i);
                break;
            default:
                assertArrayEquals((Object[]) expected, (Object[]) actual, "Unexpected value at " + i);
        }
    }
}
