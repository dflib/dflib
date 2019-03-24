package com.nhl.dflib;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DFAsserts {

    private IndexPosition[] expectedColumns;
    private List<Object[]> rows;

    public DFAsserts(DataFrame df, Index expectedColumns) {
        this(df, expectedColumns.getPositions());
    }

    public DFAsserts(DataFrame df, List<String> expectedColumns) {
        this(df, expectedColumns.toArray(new String[expectedColumns.size()]));
    }

    public DFAsserts(DataFrame df, String... expectedColumns) {
        this(df, Index.continuousPositions(expectedColumns));
    }

    public DFAsserts(DataFrame df, IndexPosition... expectedColumns) {

        assertNotNull("DataFrame is null", df);
        assertArrayEquals("DataFrame columns differ from expected", expectedColumns, df.getColumns().getPositions());

        this.expectedColumns = expectedColumns;
        this.rows = new ArrayList<>();

        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(df.getColumns());
        df.forEach(r -> {
            r.copy(rowBuilder, 0);
            rows.add(rowBuilder.reset());
        });
    }

    public DFAsserts expectHeight(int expectedHeight) {
        assertEquals("Unexpected DataFrame height", expectedHeight, rows.size());
        return this;
    }

    public DFAsserts expectRow(int pos, Object... expectedValues) {

        Object[] row = rows.get(pos);

        for (int i = 0; i < expectedColumns.length; i++) {

            IndexPosition c = expectedColumns[i];
            Object a = c.get(row);
            Object e = expectedValues[i];

            if (e == null) {
                assertNull("Unexpected value at " + c, a);
            } else if (e.getClass().isArray()) {
                assertTrue("Was expecting array at " + c, a.getClass().isArray());
                expectArrayRow(c, e, a);
            } else {
                assertEquals("Unexpected value at " + c, e, a);
            }
        }

        return this;
    }

    private void expectArrayRow(IndexPosition column, Object expected, Object actual) {

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
