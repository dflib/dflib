package org.dflib.print;

import org.dflib.DataFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFrameInlinePrintWorkerTest {

    private DataFrame df;

    @BeforeEach
    public void initDataFrameParts() {
        this.df = DataFrame.foldByRow("col1", "column2").of(
                "one", 1,
                "two", 2,
                "three", 3,
                "four", 4);
    }

    @Test
    public void toString_NoRows() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameInlinePrintWorker(out, 5, 10).print(DataFrame.empty("a", "b"));
        assertEquals("a:,b:", out.toString());
    }

    @Test
    public void toString_Normal() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameInlinePrintWorker(out, 5, 10).print(df);
        assertEquals("{col1:one,column2:1},{col1:two,column2:2},{col1:three,column2:3},{col1:four,column2:4}", out.toString());
    }

    @Test
    public void toString_TruncateRows() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameInlinePrintWorker(out, 2, 10).print(df);
        assertEquals("{col1:one,column2:1},...,{col1:four,column2:4}", out.toString());
    }

    @Test
    public void toString_TruncateColumns() throws IOException {
        StringBuilder out = new StringBuilder();
        new DataFrameInlinePrintWorker(out, 5, 4).print(df);
        assertEquals("{col1:one,c..2:1},{col1:two,c..2:2},{col1:t..e,c..2:3},{col1:four,c..2:4}", out.toString());
    }
}
