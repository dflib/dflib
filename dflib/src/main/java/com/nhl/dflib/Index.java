package com.nhl.dflib;

import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.map.DataRowToValueMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An "index" of the DataFrame that provides access to column (and in the future potentially row) metadata.
 */
public abstract class Index implements Iterable<IndexPosition> {

    protected IndexPosition[] positions;
    private Map<String, IndexPosition> positionsIndex;

    protected Index(IndexPosition... positions) {
        this.positions = positions;
    }


    protected static IndexPosition[] continuousPositions(String... names) {
        IndexPosition[] positions = new IndexPosition[names.length];
        for (int i = 0; i < names.length; i++) {
            positions[i] = new IndexPosition(i, i, names[i]);
        }

        return positions;
    }

    protected static boolean isContinuous(IndexPosition... positions) {

        // true if starts with zero and increments by one

        if (positions.length > 0 && positions[0].rowIndex() > 0) {
            return false;
        }

        for (int i = 1; i < positions.length; i++) {
            if (positions[i].rowIndex() != positions[i - 1].rowIndex() + 1) {
                return false;
            }
        }

        return true;
    }

    public static Index withNames(String... names) {
        // TODO: dedupe names like "selectNames" does?
        return new ContinuousIndex(continuousPositions(names));
    }

    public static Index withPositions(IndexPosition... positions) {
        return isContinuous(positions) ? new ContinuousIndex(positions) : new SparseIndex(positions);
    }

    @Override
    public Iterator<IndexPosition> iterator() {
        return new Iterator<IndexPosition>() {

            private final int len = size();
            private int counter = 0;

            @Override
            public boolean hasNext() {
                return counter < len;
            }

            @Override
            public IndexPosition next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Past the end of the iterator");
                }

                return positions[counter++];
            }
        };
    }

    public abstract Object[] compactCopy(Object[] row, Object[] to, int toOffset);

    public abstract Index rename(Map<String, String> oldToNewNames);

    public Index rename(String... newNames) {

        if (newNames.length != size()) {
            throw new IllegalArgumentException("New columns length does not match existing index size: "
                    + newNames.length + " vs " + size());
        }

        Map<String, String> nameMap = new HashMap<>((int) (newNames.length / 0.75));
        for (int i = 0; i < newNames.length; i++) {
            nameMap.put(positions[i].name(), newNames[i]);
        }

        return rename(nameMap);
    }

    public abstract Index compactIndex();

    public Object get(Object[] row, String columnName) {
        return position(columnName).get(row);
    }

    public Object get(Object[] row, int columnPos) {
        return getPositions()[columnPos].get(row);
    }

    public Index addNames(String... extraNames) {
        return HConcat.zipIndex(this, withNames(extraNames));
    }

    public <VR> Object[] addValues(Object[] row, DataRowToValueMapper<VR>... valueProducers) {

        int oldWidth = size();
        int expansionWidth = valueProducers.length;

        Object[] expandedRow = compactCopy(row, new Object[oldWidth + expansionWidth], 0);

        for (int i = 0; i < expansionWidth; i++) {
            expandedRow[oldWidth + i] = valueProducers[i].map(this, row);
        }

        return expandedRow;
    }

    public Index selectNames(String... names) {

        int len = names.length;
        IndexPosition[] positions = new IndexPosition[len];
        Set<String> dedupeNames = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {
            IndexPosition p = position(names[i]);

            String name = names[i];

            while (!dedupeNames.add(name)) {
                name = name + "_";
            }

            positions[i] = new IndexPosition(i, p.rowIndex(), name);
        }

        return Index.withPositions(positions);
    }

    public Index dropNames(String... names) {

        if (names.length == 0) {
            return this;
        }

        if (positionsIndex == null) {
            this.positionsIndex = computePositions();
        }

        List<IndexPosition> toDrop = new ArrayList<>(names.length);
        for (String n : names) {
            IndexPosition ip = positionsIndex.get(n);
            if (ip != null) {
                toDrop.add(ip);
            }
        }

        if (toDrop.isEmpty()) {
            return this;
        }

        IndexPosition[] toKeep = new IndexPosition[size() - toDrop.size()];
        for (int i = 0, j = 0; i < positions.length; i++) {

            IndexPosition p = positions[i];

            if (!toDrop.contains(p)) {
                toKeep[j] = new IndexPosition(j, p.rowIndex(), p.name());
                j++;
            }
        }

        return Index.withPositions(toKeep);
    }

    public IndexPosition[] getPositions() {
        return positions;
    }

    public int size() {
        return positions.length;
    }

    public IndexPosition position(String name) {
        if (positionsIndex == null) {
            this.positionsIndex = computePositions();
        }

        IndexPosition pos = positionsIndex.get(name);
        if (pos == null) {
            throw new IllegalArgumentException("Name '" + name + "' is not present in the Index");
        }

        return pos;
    }

    public boolean hasName(String names) {
        if (positionsIndex == null) {
            this.positionsIndex = computePositions();
        }

        return positionsIndex.containsKey(names);
    }

    private Map<String, IndexPosition> computePositions() {

        Map<String, IndexPosition> index = new LinkedHashMap<>();

        for (int i = 0; i < positions.length; i++) {
            IndexPosition previous = index.put(positions[i].name(), positions[i]);
            if (previous != null) {
                throw new IllegalStateException("Duplicate position name '"
                        + positions[i].name()
                        + "'. Found at " + previous + " and " + i);
            }
        }
        return index;
    }
}
