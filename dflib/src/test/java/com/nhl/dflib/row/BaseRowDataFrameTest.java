package com.nhl.dflib.row;

import com.nhl.dflib.Index;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class BaseRowDataFrameTest {

    @Test
    public void testHeight() {
        BaseRowDataFrame df = new BaseRowDataFrameImpl(Index.withNames("a", "b"), 2);
        assertEquals(2, df.height());
    }

    @Test
    public void testToString() {
        BaseRowDataFrame df = new BaseRowDataFrameImpl(Index.withNames("a", "b"), 0);
        assertEquals("BaseRowDataFrameImpl []", df.toString());
    }

    private static class BaseRowDataFrameImpl extends BaseRowDataFrame {

        private int rows;

        public BaseRowDataFrameImpl(Index columns, int rows) {
            super(columns);
            this.rows = rows;
        }

        @Override
        public Iterator<RowProxy> iterator() {

            List<Object[]> data = new ArrayList<>(rows);
            for (int i = 0; i < rows; i++) {
                data.add(new Object[columns.size()]);
            }
            return RowIterator.overArrays(columns, data);
        }
    }
}
