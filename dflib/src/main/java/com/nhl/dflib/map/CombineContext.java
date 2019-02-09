package com.nhl.dflib.map;

import com.nhl.dflib.Index;

public class CombineContext {

    private Index leftIndex;
    private Index rightIndex;
    private Index combinedIndex;

    public CombineContext(Index leftIndex, Index rightIndex, Index combinedIndex) {
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
        this.combinedIndex = combinedIndex;
    }

    public Index getLeftIndex() {
        return leftIndex;
    }

    public Index getRightIndex() {
        return rightIndex;
    }

    public Index getCombinedIndex() {
        return combinedIndex;
    }

    public Object getLeft(Object[] leftRow, String columnName) {
        return leftIndex.position(columnName).get(leftRow);
    }

    public Object getLeft(Object[] leftRow, int columnPos) {
        return leftIndex.getPositions()[columnPos].get(leftRow);
    }

    public Object getRight(Object[] rightRow, String columnName) {
        return rightIndex.position(columnName).get(rightRow);
    }

    public Object getRight(Object[] rightRow, int columnPos) {
        return rightIndex.getPositions()[columnPos].get(rightRow);
    }
}
