package org.dflib.echarts.render.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RendererTest {

    @Test
    void quoteAndEscape() {
        assertEquals("null", Renderer.quotedValue(null));
        assertEquals("345", Renderer.quotedValue(345));
        assertEquals("345.00", Renderer.quotedValue(new BigDecimal("345.00")));

        assertEquals("false", Renderer.quotedValue(false));
        assertEquals("true", Renderer.quotedValue(true));

        assertEquals("'abc'", Renderer.quotedValue("abc"));
        assertEquals("'a\"bc'", Renderer.quotedValue("a\"bc"));
        assertEquals("'\\'a\\'b\\'c\\''", Renderer.quotedValue("'a'b'c'"));
    }
}
