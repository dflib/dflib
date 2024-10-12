package org.dflib.junit5;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.Index;
import org.dflib.IntSeries;
import org.dflib.LongSeries;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataFrameAsserts {

    private String[] expectedColumns;
    private DataFrame df;

    public DataFrameAsserts(DataFrame df, Index expectedColumns) {
        this(df, expectedColumns.toArray());
    }

    public DataFrameAsserts(DataFrame df, List<String> expectedColumns) {
        this(df, expectedColumns.toArray(new String[expectedColumns.size()]));
    }

    public DataFrameAsserts(DataFrame df, String... expectedColumns) {

        assertNotNull(df, "DataFrame is null");
        assertArrayEquals(expectedColumns, df.getColumnsIndex().toArray(), "DataFrame columns differ from expected");

        this.expectedColumns = expectedColumns;
        this.df = df;
    }

    public DataFrameAsserts expectHeight(int expectedHeight) {
        assertEquals(expectedHeight, df.height(), "Unexpected DataFrame height");
        return this;
    }

    public DataFrameAsserts expectIntColumns(int... positions) {

        for (int i = 0; i < positions.length; i++) {
            assertTrue(df.getColumn(positions[i]).unsafeCastAs(Integer.class) instanceof IntSeries);
        }
        return this;
    }

    public DataFrameAsserts expectIntColumns(String... labels) {
        for (int i = 0; i < labels.length; i++) {
            assertTrue(df.getColumn(labels[i]).unsafeCastAs(Integer.class) instanceof IntSeries);
        }
        return this;
    }

    public DataFrameAsserts expectLongColumns(int... positions) {

        for (int i = 0; i < positions.length; i++) {
            assertTrue(df.getColumn(positions[i]).unsafeCastAs(Long.class) instanceof LongSeries);
        }
        return this;
    }

    public DataFrameAsserts expectLongColumns(String... labels) {
        for (int i = 0; i < labels.length; i++) {
            assertTrue(df.getColumn(labels[i]).unsafeCastAs(Long.class) instanceof LongSeries);
        }
        return this;
    }

    public DataFrameAsserts expectDoubleColumns(int... positions) {

        for (int i = 0; i < positions.length; i++) {
            assertTrue(df.getColumn(positions[i]).unsafeCastAs(Double.class) instanceof DoubleSeries);
        }
        return this;
    }

    public DataFrameAsserts expectDoubleColumns(String... labels) {
        for (int i = 0; i < labels.length; i++) {
            assertTrue(df.getColumn(labels[i]).unsafeCastAs(Double.class) instanceof DoubleSeries);
        }
        return this;
    }

    public DataFrameAsserts expectBooleanColumns(int... positions) {

        for (int i = 0; i < positions.length; i++) {
            assertTrue(df.getColumn(positions[i]).unsafeCastAs(Boolean.class) instanceof BooleanSeries);
        }
        return this;
    }

    public DataFrameAsserts expectBooleanColumns(String... labels) {
        for (int i = 0; i < labels.length; i++) {
            assertTrue(df.getColumn(labels[i]).unsafeCastAs(Boolean.class) instanceof BooleanSeries);
        }
        return this;
    }

    public DataFrameAsserts expectColumn(int pos, Object... expectedValues) {
        new SeriesAsserts(df.getColumn(pos)).expectData(expectedValues);
        return this;
    }

    public DataFrameAsserts expectColumn(String column, Object... expectedValues) {
        new SeriesAsserts(df.getColumn(column)).expectData(expectedValues);
        return this;
    }

    public DataFrameAsserts expectRow(int pos, Object... expectedValues) {

        assertTrue(pos < df.height(), () -> "Row position " + pos + " is outside the DataFrame range of 0.." + (df.height() - 1));

        // handling nulls in "vararg" specifics... caller passing a "null" results in null array instead of a single
        // element array with null... need to fix that
        Object[] expectedNormal = expectedValues != null ? expectedValues : new Object[]{null};

        assertEquals(expectedNormal.length, expectedColumns.length, () -> "Width mismatch between the expected and actual rows");

        for (int i = 0; i < expectedColumns.length; i++) {

            String column = expectedColumns[i];
            Object expected = expectedNormal[i];
            Object actual = df.getColumn(i).get(pos);

            if (expected == null) {
                assertNull(actual, "Unexpected value in '" + column + "'");
            } else if (expected.getClass().isArray()) {
                assertTrue(actual.getClass().isArray(), () -> "Was expecting array in '" + column + "': " + actual.getClass().getName());
                expectArrayRow(column, expected, actual);
            } else {
                assertEquals(expected, actual, "Unexpected value in '" + column + "'");
            }
        }

        return this;
    }

    @SafeVarargs
    public final DataFrameAsserts assertRow(int pos, Consumer<?>... valueAsserts) {
        Objects.requireNonNull(valueAsserts);

        assertEquals(expectedColumns.length, valueAsserts.length,
                "The number of assert arguments must be equal to the number of DataFrame columns.");

        for (int i = 0; i < expectedColumns.length; i++) {
            Consumer anAssert = valueAsserts[i];

            Objects.requireNonNull(anAssert);

            Object actual = df.getColumn(i).get(pos);
            anAssert.accept(actual);
        }

        return this;
    }

    private void expectArrayRow(String column, Object expected, Object actual) {

        String eArrayClass = expected.getClass().getSimpleName();
        String aArrayClass = actual.getClass().getSimpleName();
        assertEquals(eArrayClass, aArrayClass, "Unexpected array type at '" + column);

        // remove []
        String type = eArrayClass.substring(0, eArrayClass.length() - 2);
        switch (type) {
            case "char":
                assertArrayEquals((char[]) expected, (char[]) actual, "Unexpected value at " + column);
                break;
            case "long":
                assertArrayEquals((long[]) expected, (long[]) actual, "Unexpected value at " + column);
                break;
            case "int":
                assertArrayEquals((int[]) expected, (int[]) actual, "Unexpected value at " + column);
                break;
            case "byte":
                assertArrayEquals((byte[]) expected, (byte[]) actual, "Unexpected value at " + column);
                break;
            case "double":
                assertArrayEquals((double[]) expected, (double[]) actual, 0.00001, "Unexpected value at " + column);
                break;
            case "float":
                assertArrayEquals((float[]) expected, (float[]) actual, 0.00001f, "Unexpected value at " + column);
                break;
            default:
                assertArrayEquals((Object[]) expected, (Object[]) actual, "Unexpected value at " + column);
        }
    }
}
