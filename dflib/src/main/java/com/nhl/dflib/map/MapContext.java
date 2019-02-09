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
        targetIndex.set(targetRow, columnPos, value);
        return this;
    }

    public Object[] target(Object... values) {

        if (values.length == targetIndex.size()) {
            return values;
        }

        if (values.length > targetIndex.size()) {
            throw new IllegalArgumentException("Provided values won't fit in the target row: "
                    + values.length + " > " + targetIndex.size());
        }

        Object[] target = new Object[targetIndex.size()];
        if (values.length > 0) {
            System.arraycopy(values, 0, target, 0, values.length);
        }

        return target;
    }

    public Object[] addValues(Object[] sourceRow, RowToValueMapper<?>... valueProducers) {

        Object[] target = new Object[targetIndex.size()];
        sourceIndex.compactCopy(sourceRow, target, 0);

        int oldWidth = sourceIndex.size();
        int expansionWidth = valueProducers.length;

        for (int i = 0; i < expansionWidth; i++) {
            target[oldWidth + i] = valueProducers[i].map(sourceIndex, sourceRow);
        }

        return target;
    }

    public <T> Object[] mapColumn(Object[] sourceRow, String name, RowToValueMapper<T> m) {
        return mapColumn(sourceRow, sourceIndex.position(name).ordinal(), m);
    }

    public <T> Object[] mapColumn(Object[] sourceRow, int sourcePos, RowToValueMapper<T> m) {
        Object[] target = new Object[targetIndex.size()];
        sourceIndex.compactCopy(sourceRow, target, 0);

        // since target is a compact version of the source, we can use "sourcePos" index directly on it
        target[sourcePos] = m.map(getSourceIndex(), sourceRow);
        return target;
    }

    public <V, VR> Object[] mapColumn(Object[] sourceRow, String name, ValueMapper<V, VR> m) {
        return mapColumn(sourceRow, sourceIndex.position(name).ordinal(), m);
    }

    public <V, VR> Object[] mapColumn(Object[] sourceRow, int sourcePos, ValueMapper<V, VR> m) {

        Object[] target = new Object[targetIndex.size()];
        sourceIndex.compactCopy(sourceRow, target, 0);

        // since target is a compact version of the source, we can use "sourcePos" index directly on it
        target[sourcePos] = m.map((V) get(sourceRow, sourcePos));
        return target;
    }
}
