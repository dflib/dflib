package com.nhl.dflib;

import com.nhl.dflib.row.RowIterator;
import com.nhl.dflib.row.RowProxy;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Testing DataFrame interface methods behavior, by not using factory methods that create subclasses that override
 * base methods.
 */
public class DataFrame_InterfaceTest {

    @Test
    public void testWidth() {
        Index i = Index.withNames("a");
        TestDF df = new TestDF(i, asList(
                new Object[]{1},
                new Object[]{2}
        ));

        assertEquals(1, df.width());
    }

    @Test
    public void testHeight() {
        Index i = Index.withNames("a");
        TestDF df = new TestDF(i, asList(
                new Object[]{1},
                new Object[]{2}
        ));

        assertEquals(2, df.height());

    }

    private static class TestDF implements DataFrame {

        private Index columns;
        private List<Object[]> data;

        public TestDF(Index columns, List<Object[]> data) {
            this.columns = columns;
            this.data = data;
        }

        @Override
        public Index getColumns() {
            return columns;
        }

        @Override
        public Iterator<RowProxy> iterator() {
            return RowIterator.overArrays(columns, data);
        }
    }
}
