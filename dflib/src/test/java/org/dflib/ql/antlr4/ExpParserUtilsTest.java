package org.dflib.ql.antlr4;

import org.dflib.ql.antlr4.ExpParserUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ExpParserUtilsTest {

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    @Test
    public void unescapeIdentifier() {
        assertEquals("abc", ExpParserUtils.unescapeIdentifier("abc"));
        assertEquals("a c", ExpParserUtils.unescapeIdentifier("a c"));
        assertEquals("a`c", ExpParserUtils.unescapeIdentifier("a``c"));
        assertEquals("a'c", ExpParserUtils.unescapeIdentifier("a'c"));
        assertEquals("a\\c", ExpParserUtils.unescapeIdentifier("a\\c"));
        assertEquals("a\"c", ExpParserUtils.unescapeIdentifier("a\"c"));
        assertEquals("Abc", ExpParserUtils.unescapeIdentifier("\u0041bc"));
    }
}
