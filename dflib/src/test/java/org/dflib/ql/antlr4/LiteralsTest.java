package org.dflib.ql.antlr4;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LiteralsTest {

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @Test
    public void unescapeIdentifier() {
        assertEquals("abc", Literals.unescapeIdentifier("abc"));
        assertEquals("a c", Literals.unescapeIdentifier("a c"));
        assertEquals("a`c", Literals.unescapeIdentifier("a``c"));
        assertEquals("a'c", Literals.unescapeIdentifier("a'c"));
        assertEquals("a\\c", Literals.unescapeIdentifier("a\\c"));
        assertEquals("a\"c", Literals.unescapeIdentifier("a\"c"));
        assertEquals("Abc", Literals.unescapeIdentifier("\u0041bc"));
    }
}
