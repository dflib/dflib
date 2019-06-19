package com.nhl.dflib.unit;

import com.nhl.dflib.Series;

import static org.junit.Assert.*;

public class SeriesAsserts {

    private Object[] data;

    public SeriesAsserts(Series<?> series) {
        assertNotNull("Series is null", series);

        this.data = new Object[series.size()];
        series.copyTo(data, 0, 0, series.size());
    }

    public SeriesAsserts expectData(Object... expectedValues) {

        assertEquals("Unexpected Series length", expectedValues.length, data.length);

        for (int i = 0; i < expectedValues.length; i++) {

            Object a = data[i];
            Object e = expectedValues[i];

            if (e == null) {
                assertNull("Unexpected value at " + i, a);
            } else if (e.getClass().isArray()) {
                assertTrue("Was expecting array at " + i, a.getClass().isArray());
                expectArrayRow(i, e, a);
            } else {
                assertEquals("Unexpected value at " + i, e, a);
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
                assertArrayEquals("Unexpected value at " + i, (char[]) expected, (char[]) actual);
                break;
            case "long":
                assertArrayEquals("Unexpected value at " + i, (long[]) expected, (long[]) actual);
                break;
            case "int":
                assertArrayEquals("Unexpected value at " + i, (int[]) expected, (int[]) actual);
                break;
            case "byte":
                assertArrayEquals("Unexpected value at " + i, (byte[]) expected, (byte[]) actual);
                break;
            case "double":
                assertArrayEquals("Unexpected value at " + i, (double[]) expected, (double[]) actual, 0.00001);
                break;
            case "float":
                assertArrayEquals("Unexpected value at " + i, (float[]) expected, (float[]) actual, 0.00001f);
                break;
            default:
                assertArrayEquals("Unexpected value at " + i, (Object[]) expected, (Object[]) actual);
        }
    }
}
