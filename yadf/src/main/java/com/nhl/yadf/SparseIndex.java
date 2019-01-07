package com.nhl.yadf;

import java.util.Map;

public class SparseIndex extends Index {

    protected SparseIndex(IndexPosition... positions) {
        super(positions);
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
    public Object[] compactCopy(Object[] row, Object[] to, int toOffset) {

        for (int i = 0; i < positions.length; i++) {
            to[toOffset + i] = positions[i].get(row);
        }

        return to;
    }

    public Index rename(Map<String, String> oldToNewNames) {

        int len = size();

        IndexPosition[] newPositions = new IndexPosition[len];
        for (int i = 0; i < len; i++) {
            IndexPosition pos = positions[i];
            String newName = oldToNewNames.get(pos.name());
            newPositions[i] = newName != null ? new IndexPosition(i, pos.rowIndex(), newName) : pos;
        }

        return new SparseIndex(newPositions);
    }
}
