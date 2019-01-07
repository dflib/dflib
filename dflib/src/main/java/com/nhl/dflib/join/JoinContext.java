package com.nhl.dflib.join;

import com.nhl.dflib.Index;

public class JoinContext {

    private Index leftIndex;
    private Index rightIndex;
    private Index joinIndex;

    public JoinContext(Index leftIndex, Index rightIndex, Index joinIndex) {
        this.leftIndex = leftIndex;
        this.rightIndex = rightIndex;
        this.joinIndex = joinIndex;
    }

    public Index getJoinIndex() {
        return joinIndex;
    }

    public Index getLeftIndex() {
        return leftIndex;
    }

    public Index getRightIndex() {
        return rightIndex;
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
