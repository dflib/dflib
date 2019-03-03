package com.nhl.dflib;

import java.util.Map;

public class SparseIndex extends Index {

    private int span;

    protected SparseIndex(IndexPosition... positions) {
        super(positions);

        int maxIndex = 0;
        for (int i = 0; i < positions.length; i++) {
            maxIndex = Math.max(positions[i].position(), maxIndex);
        }

        this.span = maxIndex + 1;
    }

    @Override
    public Index compactIndex() {
        String[] names = new String[positions.length];
        for (int i = 0; i < positions.length; i++) {
            names[i] = positions[i].name();
        }
        return withNames(names);
    }

    @Override
    public boolean isCompact() {
        return false;
    }

    @Override
    public int span() {
        return span;
    }

    public Index rename(Map<String, String> oldToNewNames) {

        int len = size();

        IndexPosition[] newPositions = new IndexPosition[len];
        for (int i = 0; i < len; i++) {
            IndexPosition pos = positions[i];
            String newName = oldToNewNames.get(pos.name());
            newPositions[i] = newName != null ? new IndexPosition(i, pos.position(), newName) : pos;
        }

        return new SparseIndex(newPositions);
    }
}
