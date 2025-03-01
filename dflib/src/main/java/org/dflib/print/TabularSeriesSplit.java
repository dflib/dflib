package org.dflib.print;

class TabularSeriesSplit {

    final TabularColumnData[] left;
    final TabularColumnData[] right;
    final boolean truncated;

    public TabularSeriesSplit(TabularColumnData[] left, TabularColumnData[] right, boolean truncated) {
        this.left = left;
        this.right = right;
        this.truncated = truncated;
    }
}
