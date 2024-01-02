package org.dflib.jupyter.render;

import org.dflib.print.TabularPrinter;

public class MutableTabularPrinter extends TabularPrinter {

    public MutableTabularPrinter(int maxDisplayRows, int maxDisplayColumnWidth) {
        super(maxDisplayRows, maxDisplayColumnWidth);
    }

    public void setMaxDisplayRows(int r) {
        this.maxDisplayRows = r;
    }

    public void setMaxDisplayColumnWidth(int w) {
        this.maxDisplayColumnWidth = w;
    }
}
