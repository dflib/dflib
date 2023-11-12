package com.nhl.dflib.print;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BasePrintWorkerTest {

    @Test
    public void truncate() {
        assertEquals("..", BasePrintWorker.truncate("abc", 1));
        assertEquals("..", BasePrintWorker.truncate("abc", 2));
        assertEquals("abc", BasePrintWorker.truncate("abc", 3));
        assertEquals("a..", BasePrintWorker.truncate("abcd", 3));
        assertEquals("abc", BasePrintWorker.truncate("abc", 4));
        assertEquals("abcd", BasePrintWorker.truncate("abcd", 4));
        assertEquals("a..e", BasePrintWorker.truncate("abcde", 4));
        assertEquals("a..f", BasePrintWorker.truncate("abcdef", 4));
        assertEquals("ab..g", BasePrintWorker.truncate("abcdefg", 5));
        assertEquals("ab..h", BasePrintWorker.truncate("abcdefgh", 5));
    }
}
