package org.dflib.jupyter.render;

import org.dflib.Series;
import org.dflib.jjava.jupyter.kernel.display.RenderContext;
import org.dflib.jjava.jupyter.kernel.display.RenderFunction;
import org.dflib.jjava.jupyter.kernel.display.mime.MIMEType;
import org.dflib.print.Printer;

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
