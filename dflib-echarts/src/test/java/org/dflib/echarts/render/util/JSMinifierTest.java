package org.dflib.echarts.render.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSMinifierTest {

    @Test
    public void minify_EmptyString() {
        assertEquals("", JSMinifier.minify(""));
    }

    @Test
    public void minify_Null() {
        assertEquals(null, JSMinifier.minify(null));
    }

    @Test
    public void minify_SimpleCode() {
        String input = "var x = 5;\nvar y = 10;";
        String expected = "var x = 5;var y = 10;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_StripLeadingSpaces() {
        String input = "    var x = 5;\n        var y = 10;\n    return x + y;";
        String expected = "var x = 5;var y = 10;return x + y;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_SingleLineComments() {
        String input = "var x = 5; // This is a comment\nvar y = 10;";
        String expected = "var x = 5;var y = 10;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_MultiLineComments() {
        String input = "var x = 5;\n/* This is a\n   multi-line comment */\nvar y = 10;";
        String expected = "var x = 5;var y = 10;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_MixedComments() {
        String input = """
                // Header comment
                var x = 5; // inline comment
                /* block comment */ var y = 10;
                /* multi
                   line
                   block */
                return x + y; // final comment
                """;
        String expected = "var x = 5;var y = 10;return x + y;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_CommentsInStrings() {
        String input = "var str = \"// this is not a comment\";\nvar str2 = '/* also not a comment */';";
        String expected = "var str = \"// this is not a comment\";var str2 = '/* also not a comment */';";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_TemplateLiterals() {
        String input = "var str = `// not a comment`;\nvar x = 5;";
        String expected = "var str = `// not a comment`;var x = 5;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_ComplexCode() {
        String input = """
                    function test() {
                        // Initialize variables
                        var x = 5;
                        /* Calculate
                           the result */
                        var y = x * 2;
                        return y;
                    }
                """;
        String expected = "function test() {var x = 5;var y = x * 2;return y;}";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_EscapedQuotes() {
        String input = "var str = 'It\\'s a test';\nvar x = 5;";
        String expected = "var str = 'It\\'s a test';var x = 5;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_CRLF() {
        String input = "var x = 5;\r\nvar y = 10;";
        String expected = "var x = 5;var y = 10;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_Tabs() {
        String input = "\t\tvar x = 5;\n\t\t\tvar y = 10;";
        String expected = "var x = 5;var y = 10;";
        assertEquals(expected, JSMinifier.minify(input));
    }

    @Test
    public void minify_MultiLineTemplateLiterals() {
        String input = """
                var html = `
                    <div>
                        // This is not a comment
                        /* Neither is this */
                        <span>Hello</span>
                    </div>
                `;
                var x = 5;
                """;
        String expected = "var html = `\n<div>\n// This is not a comment\n/* Neither is this */\n<span>Hello</span>\n</div>\n`;var x = 5;";
        assertEquals(expected, JSMinifier.minify(input));
    }
}
