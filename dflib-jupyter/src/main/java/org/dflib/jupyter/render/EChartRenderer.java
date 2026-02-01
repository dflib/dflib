package org.dflib.jupyter.render;

import org.dflib.echarts.EChartHtml;
import org.dflib.echarts.ECharts;
import org.dflib.jjava.jupyter.kernel.display.RenderContext;
import org.dflib.jjava.jupyter.kernel.display.RenderFunction;
import org.dflib.jjava.jupyter.kernel.display.mime.MIMEType;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Renders {@link EChartHtml} object as HTML with embedded JS.
 */
public class EChartRenderer implements RenderFunction<EChartHtml> {

    static final Set<MIMEType> SUPPORTED_TYPES = Set.of(MIMEType.TEXT_HTML, MIMEType.TEXT_PLAIN);

    // after reaching this threshold, just re-render every chart instead of keeping an ever-increasing collection
    // of ids in memory.
    static final int MAX_CHARTS_TO_TRACK = 1000;

    private final AtomicInteger idCacheSlots;
    private final ConcurrentMap<String, String> seenCharts;

    public EChartRenderer() {
        // allocate a small map initially. There may be no or very few charts to render
        this.seenCharts = new ConcurrentHashMap<>();
        this.idCacheSlots = new AtomicInteger(MAX_CHARTS_TO_TRACK);
    }

    @Override
    public void render(EChartHtml chart, RenderContext context) {
        boolean canRender = SUPPORTED_TYPES.stream().anyMatch(context::wantsDataRenderedAs);

        if (canRender) {
            // We must ensure a unique chart <div> ID to prevent JavaScript conflicts on the Notebook page.
            EChartHtml chartToRender = shouldResetDivId(chart.getChartDivId()) ? chart.plotWithNewDivId() : chart;
            String content = toString(chartToRender);
            SUPPORTED_TYPES.forEach(t -> context.renderIfRequested(t, () -> content));
        }
    }

    boolean shouldResetDivId(String divId) {

        // Memorize previously seen IDs to prevent unneeded chart cloning and re-rendering, but only to a certain
        // threshold to prevent memory leaks in the renderer

        if (idCacheSlots.get() <= 0) {
            return true;
        }

        String existingId = seenCharts.putIfAbsent(divId, divId);
        if (existingId != null) {
            return true;
        }

        if (idCacheSlots.decrementAndGet() <= 0) {
            seenCharts.clear();
        }

        return false;
    }

    private String toString(EChartHtml chart) {
        StringBuilder out = new StringBuilder();

        ECharts.saver()
                .defaultEmbeddedTemplate()
                .save(out, chart);

        return out.toString();
    }
}
