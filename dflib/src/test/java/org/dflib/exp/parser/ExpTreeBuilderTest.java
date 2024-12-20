package org.dflib.exp.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExpTreeBuilderTest {

    @Test
    void resolveEscapes() {
        assertEquals("abc", ExpTreeBuilder.resolveEscapes("abc"));
        assertEquals("\n", ExpTreeBuilder.resolveEscapes("\\n"));
        assertEquals("\n\r \t", ExpTreeBuilder.resolveEscapes("\\n\\r\\s\\t"));
        assertEquals("abc\n", ExpTreeBuilder.resolveEscapes("abc\\n"));
        assertEquals("\nabc", ExpTreeBuilder.resolveEscapes("\\nabc"));
        assertEquals("\nabc\n", ExpTreeBuilder.resolveEscapes("\\nabc\\n"));
    }
}