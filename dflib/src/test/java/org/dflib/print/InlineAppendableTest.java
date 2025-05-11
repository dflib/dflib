package org.dflib.print;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InlineAppendableTest {

    @Test
    public void truncate() {
        assertEquals("..", InlineAppendable.truncate("abc", 1));
        assertEquals("..", InlineAppendable.truncate("abc", 2));
        assertEquals("abc", InlineAppendable.truncate("abc", 3));
        assertEquals("a..", InlineAppendable.truncate("abcd", 3));
        assertEquals("abc", InlineAppendable.truncate("abc", 4));
        assertEquals("abcd", InlineAppendable.truncate("abcd", 4));
        assertEquals("a..e", InlineAppendable.truncate("abcde", 4));
        assertEquals("a..f", InlineAppendable.truncate("abcdef", 4));
        assertEquals("ab..g", InlineAppendable.truncate("abcdefg", 5));
        assertEquals("ab..h", InlineAppendable.truncate("abcdefgh", 5));
    }
}
