package com.nhl.dflib.map;

import com.nhl.dflib.Index;

public class MapContext {

    private Index sourceIndex;
    private Index targetIndex;

    public MapContext(Index sourceIndex, Index targetIndex) {
        this.sourceIndex = sourceIndex;
        this.targetIndex = targetIndex;
    }

    public Index getSourceIndex() {
        return sourceIndex;
    }

    public Index getTargetIndex() {
        return targetIndex;
    }

    public Object get(Object[] sourceRow, String columnName) {
        return sourceIndex.position(columnName).get(sourceRow);
    }

    public Object get(Object[] sourceRow, int columnPos) {
        return sourceIndex.getPositions()[columnPos].get(sourceRow);
    }

    public MapContext set(Object[] targetRow, String columnName, Object value) {
        targetIndex.set(targetRow, columnName, value);
        return this;
    }

    public MapContext set(Object[] targetRow, int columnPos, Object value) {
        // TODO: should we assuming the target array is compact, and set it directly on the array?
        //  or are we second-guessing the compiler here?
        targetIndex.set(targetRow, columnPos, value);
        return this;
    }
}
