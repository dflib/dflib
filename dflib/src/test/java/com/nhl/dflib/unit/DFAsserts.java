package com.nhl.dflib.unit;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DFAsserts {

    private String[] expectedColumns;
    private DataFrame df;
    private List<Object[]> rows;

    public DFAsserts(DataFrame df, Index expectedColumns) {
        this(df, expectedColumns.getLabels());
    }

    public DFAsserts(DataFrame df, List<String> expectedColumns) {
        this(df, expectedColumns.toArray(new String[expectedColumns.size()]));
    }

    public DFAsserts(DataFrame df, String... expectedColumns) {

        assertNotNull("DataFrame is null", df);
        assertArrayEquals("DataFrame columns differ from expected", expectedColumns, df.getColumnsIndex().getLabels());

        this.expectedColumns = expectedColumns;
        this.rows = new ArrayList<>();
        this.df = df;

        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(df.getColumnsIndex());
        df.forEach(r -> {
            r.copy(rowBuilder, 0);
            rows.add(rowBuilder.reset());
        });
    }

    public DFAsserts expectHeight(int expectedHeight) {
        assertEquals("Unexpected DataFrame height", expectedHeight, rows.size());
        return this;
    }

    public DFAsserts expectIntColumns(int... positions) {

        for (int i = 0; i < positions.length; i++) {
            // the assertion is superfluous ... "getColumnAsInt" throws if the column is not an IntSeries
            assertTrue(df.getColumnAsInt(positions[i]) instanceof IntSeries);
        }
        return this;
    }

    public DFAsserts expectIntColumns(String... labels) {
        for (int i = 0; i < labels.length; i++) {

            // the assertion is superfluous ... "getColumnAsInt" throws if the column is not an IntSeries
            assertTrue(df.getColumnAsInt(labels[i]) instanceof IntSeries);
        }
        return this;
    }

    public DFAsserts expectLongColumns(int... positions) {

        for (int i = 0; i < positions.length; i++) {
            // the assertion is superfluous ... "getColumnAsLong" throws if the column is not an LongSeries
            assertTrue(df.getColumnAsLong(positions[i]) instanceof LongSeries);
        }
        return this;
    }

    public DFAsserts expectLongColumns(String... labels) {
        for (int i = 0; i < labels.length; i++) {

            // the assertion is superfluous ... "getColumnAsLong" throws if the column is not an LongSeries
            assertTrue(df.getColumnAsLong(labels[i]) instanceof LongSeries);
        }
        return this;
    }

    public DFAsserts expectDoubleColumns(int... positions) {

        for (int i = 0; i < positions.length; i++) {
            // the assertion is superfluous ... "getColumnAsDouble" throws if the column is not an DoubleSeries
            assertTrue(df.getColumnAsDouble(positions[i]) instanceof DoubleSeries);
        }
        return this;
    }

    public DFAsserts expectDoubleColumns(String... labels) {
        for (int i = 0; i < labels.length; i++) {

            // the assertion is superfluous ... "getColumnAsInt" throws if the column is not an IntSeries
            assertTrue(df.getColumnAsDouble(labels[i]) instanceof DoubleSeries);
        }
        return this;
    }

    public DFAsserts expectBooleanColumns(int... positions) {

        for (int i = 0; i < positions.length; i++) {
            // the assertion is superfluous ... "getColumnAsBoolean" throws if the column is not an BooleanSeries
            assertTrue(df.getColumnAsBoolean(positions[i]) instanceof BooleanSeries);
        }
        return this;
    }

    public DFAsserts expectBooleanColumns(String... labels) {
        for (int i = 0; i < labels.length; i++) {

            // the assertion is superfluous ... "getColumnAsBoolean" throws if the column is not an BooleanSeries
            assertTrue(df.getColumnAsBoolean(labels[i]) instanceof BooleanSeries);
        }
        return this;
    }

    public DFAsserts expectRow(int pos, Object... expectedValues) {

        Object[] row = rows.get(pos);

        for (int i = 0; i < expectedColumns.length; i++) {

            String c = expectedColumns[i];
            Object a = row[i];
            Object e = expectedValues[i];

            if (e == null) {
                assertNull("Unexpected value in '" + c + "'", a);
            } else if (e.getClass().isArray()) {
                assertTrue("Was expecting array in '" + c + "'", a.getClass().isArray());
                expectArrayRow(c, e, a);
            } else {
                assertEquals("Unexpected value in '" + c + "'", e, a);
            }
        }

        return this;
    }

    private void expectArrayRow(String column, Object expected, Object actual) {

        String eArrayClass = expected.getClass().getSimpleName();
        String aArrayClass = actual.getClass().getSimpleName();
        assertEquals("Unexpected array type at '" + column, eArrayClass, aArrayClass);

        // remove []
        String type = eArrayClass.substring(0, eArrayClass.length() - 2);
        switch (type) {
            case "char":
                assertArrayEquals("Unexpected value at " + column, (char[]) expected, (char[]) actual);
                break;
            case "long":
                assertArrayEquals("Unexpected value at " + column, (long[]) expected, (long[]) actual);
                break;
            case "int":
                assertArrayEquals("Unexpected value at " + column, (int[]) expected, (int[]) actual);
                break;
            case "byte":
                assertArrayEquals("Unexpected value at " + column, (byte[]) expected, (byte[]) actual);
                break;
            case "double":
                assertArrayEquals("Unexpected value at " + column, (double[]) expected, (double[]) actual, 0.00001);
                break;
            case "float":
                assertArrayEquals("Unexpected value at " + column, (float[]) expected, (float[]) actual, 0.00001f);
                break;
            default:
                assertArrayEquals("Unexpected value at " + column, (Object[]) expected, (Object[]) actual);
        }
    }
}
