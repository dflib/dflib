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
        targetIndex.position(columnName).set(targetRow, value);
        return this;
    }

    public MapContext set(Object[] targetRow, int columnPos, Object value) {
        targetIndex.getPositions()[columnPos].set(targetRow, value);
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

    public Object[] copyToTarget(Object[] sourceRow) {
        return copyToTarget(sourceRow, 0);
    }

    public <T> Object[] mapColumn(Object[] sourceRow, String name, DataRowToValueMapper<T> m) {
        return mapColumn(sourceRow, sourceIndex.position(name).ordinal(), m);
    }

    public <T> Object[] mapColumn(Object[] sourceRow, int sourcePos, DataRowToValueMapper<T> m) {
        Object[] target = copyToTarget(sourceRow);

        // since target is a compact version of the source, we can use "sourcePos" index directly on it
        target[sourcePos] = m.map(this, sourceRow);
        return target;
    }

    public <V, VR> Object[] mapColumn(Object[] sourceRow, String name, ValueMapper<V, VR> m) {
        return mapColumn(sourceRow, sourceIndex.position(name).ordinal(), m);
    }

    public <V, VR> Object[] mapColumn(Object[] sourceRow, int sourcePos, ValueMapper<V, VR> m) {
        Object[] target = copyToTarget(sourceRow);

        // since target is a compact version of the source, we can use "sourcePos" index directly on it
        target[sourcePos] = m.map((V) get(sourceRow, sourcePos));
        return target;
    }

    public Object[] copyToTarget(Object[] sourceRow, int targetOffset) {
        Object[] target = target();

        if (targetOffset > target.length) {
            return target;
        }

        System.arraycopy(sourceRow, 0, target, targetOffset, Math.min(target.length - targetOffset, sourceRow.length));
        return target;
    }
}
