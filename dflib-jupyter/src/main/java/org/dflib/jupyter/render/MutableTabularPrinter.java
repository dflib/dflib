package org.dflib.jupyter.render;

import org.dflib.print.TabularPrinter;

public class MutableTabularPrinter extends TabularPrinter {

    /**
     * @since 2.0.0
     */
    public MutableTabularPrinter() {
        // using regular printer defaults
    }

    public MutableTabularPrinter(int maxRows, int maxCols, int maxValueChars) {
        super(maxRows, maxCols, maxValueChars);
    }

    public void setMaxDisplayRows(int r) {
        this.maxRows = r;
    }

    /**
     * @since 2.0.0
     */
    public void setMaxDisplayCols(int c) {
        this.maxCols = c;
    }

    /**
     * @since 2.0.0
     */
    public void setMaxDisplayValueWidth(int w) {
        this.maxValueChars = w;
    }

    /**
     * @deprecated in favor of {@link #setMaxDisplayValueWidth(int)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public void setMaxDisplayColumnWidth(int w) {
        setMaxDisplayValueWidth(w);
    }
}
