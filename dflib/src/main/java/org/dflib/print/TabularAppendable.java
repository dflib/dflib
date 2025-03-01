package org.dflib.print;

import java.io.IOException;

abstract class TabularAppendable {

    protected Appendable out;
    protected int maxDisplayColumnWidth;
    protected int maxDisplayRows;

    public TabularAppendable(Appendable out, int maxDisplayRows, int maxDisplayColumnWidth) {
        this.out = out;
        this.maxDisplayColumnWidth = maxDisplayColumnWidth;
        this.maxDisplayRows = maxDisplayRows;
    }

    protected void printNewLine() throws IOException {
        out.append(System.lineSeparator());
    }
}
