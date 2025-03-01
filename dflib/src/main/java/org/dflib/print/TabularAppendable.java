package org.dflib.print;

import java.io.IOException;

abstract class TabularAppendable {

    protected Appendable out;
    protected int maxColumnChars;
    protected int maxRows;

    public TabularAppendable(Appendable out, int maxRows, int maxColumnChars) {
        this.out = out;
        this.maxColumnChars = maxColumnChars;
        this.maxRows = maxRows;
    }

    protected void printNewLine() throws IOException {
        out.append(System.lineSeparator());
    }
}
