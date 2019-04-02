package com.nhl.dflib.print;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataFramePrintWorkerTest {

    @Test
    public void testTruncate() {
        assertEquals("..", DataFramePrintWorker.truncate("abc", 1));
        assertEquals("..", DataFramePrintWorker.truncate("abc", 2));
        assertEquals("abc", DataFramePrintWorker.truncate("abc", 3));
        assertEquals("a..", DataFramePrintWorker.truncate("abcd", 3));
        assertEquals("abc", DataFramePrintWorker.truncate("abc", 4));
        assertEquals("abcd", DataFramePrintWorker.truncate("abcd", 4));
        assertEquals("a..e", DataFramePrintWorker.truncate("abcde", 4));
        assertEquals("a..f", DataFramePrintWorker.truncate("abcdef", 4));
        assertEquals("ab..g", DataFramePrintWorker.truncate("abcdefg", 5));
        assertEquals("ab..h", DataFramePrintWorker.truncate("abcdefgh", 5));
    }
}
