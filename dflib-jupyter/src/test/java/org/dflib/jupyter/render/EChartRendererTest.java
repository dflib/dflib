package org.dflib.jupyter.render;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EChartRendererTest {


    @Test
    void shouldResetDivId() {
        EChartRenderer renderer = new EChartRenderer();

        assertFalse(renderer.shouldResetDivId("d1"));
        assertTrue(renderer.shouldResetDivId("d1"));
        assertTrue(renderer.shouldResetDivId("d1"));
        assertFalse(renderer.shouldResetDivId("d2"));
    }

    @Test
    void shouldResetDivId_Limit() {
        EChartRenderer renderer = new EChartRenderer();

        for (int i = 0; i < EChartRenderer.MAX_CHARTS_TO_TRACK; i++) {
            assertFalse(renderer.shouldResetDivId("d" + i));
        }

        assertTrue(renderer.shouldResetDivId("d2"));
        assertTrue(renderer.shouldResetDivId("d500"));
        assertTrue(renderer.shouldResetDivId("d2000"), "Once the threshold is exceeded, all charts' ids should be reset");
    }
}
