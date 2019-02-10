package com.nhl.dflib;

import com.nhl.dflib.row.RowBuilder;

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
    public void compactCopy(Object[] from, RowBuilder to, int toOffset) {
        for (int i = 0; i < positions.length; i++) {
            to.set(i + toOffset, positions[i].get(from));
        }
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
