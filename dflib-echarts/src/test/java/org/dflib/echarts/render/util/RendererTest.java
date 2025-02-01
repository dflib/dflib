package org.dflib.echarts.render.util;

import org.dflib.echarts.render.ContainerModel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RendererTest {

    @Test
    public void renderContainer() {

        String s1 = Renderer.renderContainer(new ContainerModel("_tid", 600, 400));
        assertEquals("<div id='_tid' class='dfl_ech' style='width: 600px;height:400px;'></div>", s1);

        String s2 =Renderer.renderContainer(new ContainerModel("_tid", 20, 10));
        assertEquals("<div id='_tid' class='dfl_ech' style='width: 20px;height:10px;'></div>", s2);
    }

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
