package org.dflib.jupyter.render;

import org.dflib.Series;
import org.dflib.print.Printer;
import io.github.spencerpark.jupyter.kernel.display.RenderContext;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;

public class SeriesRenderer implements RenderFunction<Series> {

    private final Printer printer;

    public SeriesRenderer(Printer printer) {
        this.printer = printer;
    }


    @Override
    public void render(Series s, RenderContext context) {
        context.renderIfRequested(MIMEType.TEXT_PLAIN, () -> printer.toString(s));
    }
}
