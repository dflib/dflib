package com.nhl.dflib;

import java.util.Map;

/**
 * An index whose IndexPosition's ordinals and rowIndexes are the same. As opposed to {@link SparseIndex} this index
 * starts from position zero and has no gaps.
 */
public class ContinuousIndex extends Index {

    protected ContinuousIndex(IndexPosition... positions) {
        super(positions);
    }

    @Override
    public Index compactIndex() {
        return this;
    }

    @Override
    public void compactCopy(Object[] row, Object[] to, int toOffset) {
        System.arraycopy(row, 0, to, toOffset, positions.length);
    }

    public Index rename(Map<String, String> oldToNewNames) {

        int len = size();

        IndexPosition[] newPositions = new IndexPosition[len];
        for (int i = 0; i < len; i++) {
            IndexPosition pos = positions[i];
            String newName = oldToNewNames.get(pos.name());
            newPositions[i] = newName != null ? new IndexPosition(i, i, newName) : pos;
        }

        return new ContinuousIndex(newPositions);
    }
}
