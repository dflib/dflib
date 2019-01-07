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

    public Object[] target(Object... values) {

        if (values.length == combinedIndex.size()) {
            return values;
        }

        if (values.length > combinedIndex.size()) {
            throw new IllegalArgumentException("Provided values won't fit in the combined row: "
                    + values.length + " > " + combinedIndex.size());
        }

        Object[] target = new Object[combinedIndex.size()];
        if (values.length > 0) {
            System.arraycopy(values, 0, target, 0, values.length);
        }

        return target;
    }

    public Object[] copyToTarget(Object[] leftRow, Object[] rightRow, int leftOffset, int rightOffset) {
        Object[] target = target();

        // rows can be null in case of outer joins...

        if (leftRow != null && leftOffset <= target.length) {
            leftIndex.compactCopy(leftRow, target, leftOffset);
        }

        if (rightRow != null && rightOffset <= target.length) {
            rightIndex.compactCopy(rightRow, target, rightOffset);
        }

        return target;
    }
}
