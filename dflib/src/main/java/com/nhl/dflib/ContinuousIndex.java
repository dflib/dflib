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
    public boolean isCompact() {
        return true;
    }

    @Override
    public int span() {
        return size();
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
