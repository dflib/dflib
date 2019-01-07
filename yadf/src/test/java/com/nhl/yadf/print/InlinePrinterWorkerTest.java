package com.nhl.yadf.print;

import com.nhl.yadf.DataRow;
import com.nhl.yadf.Index;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class InlinePrinterWorkerTest {

    private Index columns;
    private List<Object[]> rows;

    @Before
    public void initDataFrameParts() {
        this.columns = Index.withNames("col1", "column2");
        this.rows = asList(
                DataRow.row("one", 1),
                DataRow.row("two", 2),
                DataRow.row("three", 3),
                DataRow.row("four", 4));
    }

    @Test
    public void testPrint_Full() {
        InlinePrinterWorker w = new InlinePrinterWorker(new StringBuilder(), 5, 10);

        assertEquals("{col1:one,column2:1},{col1:two,column2:2},{col1:three,column2:3},{col1:four,column2:4}",
                w.print(columns, rows.iterator()).toString());
    }

    @Test
    public void testPrint_TruncateRows() {
        InlinePrinterWorker w = new InlinePrinterWorker(new StringBuilder(), 2, 10);
        assertEquals("{col1:one,column2:1},{col1:two,column2:2},...", w.print(columns, rows.iterator()).toString());
    }

    @Test
    public void testPrint_TruncateColumns() {
        InlinePrinterWorker w = new InlinePrinterWorker(new StringBuilder(), 5, 4);
        assertEquals("{col1:one,c..2:1},{col1:two,c..2:2},{col1:t..e,c..2:3},{col1:four,c..2:4}",
                w.print(columns, rows.iterator()).toString());
    }
}
