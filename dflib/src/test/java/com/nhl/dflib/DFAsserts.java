package com.nhl.dflib;

import com.nhl.dflib.row.ArrayRowBuilder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DFAsserts {

    private IndexPosition[] expectedColumns;
    private List<Object[]> rows;

    public DFAsserts(DataFrame df, Index expectedColumns) {
        this(df, expectedColumns.getPositions());
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
            r.copyAll(rowBuilder, 0);
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
            assertEquals("Unexpected value for '" + expectedColumns[i] + " (" + i + ")'",
                    expectedValues[i],
                    expectedColumns[i].get(row));
        }

        return this;
    }
}
