package com.nhl.yadf.print;

import org.junit.Test;

import static org.junit.Assert.*;

public class BasePrinterWorkerTest {

    @Test
    public void testTruncate() {
        assertEquals("..", BasePrinterWorker.truncate("abc", 1));
        assertEquals("..", BasePrinterWorker.truncate("abc", 2));
        assertEquals("abc", BasePrinterWorker.truncate("abc", 3));
        assertEquals("a..", BasePrinterWorker.truncate("abcd", 3));
        assertEquals("abc", BasePrinterWorker.truncate("abc", 4));
        assertEquals("abcd", BasePrinterWorker.truncate("abcd", 4));
        assertEquals("a..e", BasePrinterWorker.truncate("abcde", 4));
        assertEquals("a..f", BasePrinterWorker.truncate("abcdef", 4));
        assertEquals("ab..g", BasePrinterWorker.truncate("abcdefg", 5));
        assertEquals("ab..h", BasePrinterWorker.truncate("abcdefgh", 5));
    }
}
