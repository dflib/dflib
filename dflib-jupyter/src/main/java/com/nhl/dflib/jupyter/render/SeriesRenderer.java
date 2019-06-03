package com.nhl.dflib.jupyter.render;

import com.nhl.dflib.Series;
import com.nhl.dflib.print.Printer;
import io.github.spencerpark.jupyter.kernel.display.RenderContext;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;

public class SeriesRenderer implements RenderFunction<Series> {

    private Printer printer;

    public SeriesRenderer(Printer printer) {
        this.printer = printer;
    }


    @Override
    public void render(Series s, RenderContext context) {
        context.renderIfRequested(MIMEType.TEXT_PLAIN, () -> printer.toString(s));
    }
}
