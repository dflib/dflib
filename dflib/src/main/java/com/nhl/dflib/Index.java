package com.nhl.dflib;

import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.series.ArrayIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An "index" of the DataFrame that provides access to column (and in the future potentially row) metadata.
 */
public class Index implements Iterable<String> {

    protected String[] labels;
    protected Map<String, Integer> labelPositions;

    protected Index(String... labels) {
        this.labels = labels;
    }

    /**
     * Creates a new index based on enum values.
     *
     * @param columns enum type that defines Index columns
     * @return a new Index with columns matching the provided Enum
     */
    public static <E extends Enum<E>> Index withLabels(Class<E> columns) {

        E[] enumValues = columns.getEnumConstants();
        String[] labels = new String[enumValues.length];
        for (int i = 0; i < enumValues.length; i++) {
            labels[i] = enumValues[i].name();
        }

        return withLabels(labels);
    }

    public static Index withLabels(String... labels) {
        // TODO: dedupe labels like "selectLabels" does?
        return new Index(labels);
    }

    @Override
    public Iterator<String> iterator() {
        return new ArrayIterator<>(labels);
    }

    public Index rename(Map<String, String> oldToNewLabels) {

        int len = size();

        String[] newLabels = new String[len];
        for (int i = 0; i < len; i++) {
            String oldLabel = labels[i];
            String newLabel= oldToNewLabels.get(oldLabel);
            newLabels[i] = newLabel != null ? newLabel : oldLabel;
        }

        return new Index(newLabels);
    }

    public Index rename(String... newLabels) {

        if (newLabels.length != size()) {
            throw new IllegalArgumentException("New columns length does not match existing index size: "
                    + newLabels.length + " vs " + size());
        }

        Map<String, String> map = new HashMap<>((int) (newLabels.length / 0.75));
        for (int i = 0; i < newLabels.length; i++) {
            map.put(labels[i], newLabels[i]);
        }

        return rename(map);
    }

    public Index addLabels(String... extraLabels) {
        return HConcat.zipIndex(this, withLabels(extraLabels));
    }

    public Index selectLabels(String... labels) {

        int len = labels.length;
        String[] selectedLabels = new String[len];
        Set<String> uniqueLabels = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {

            // this will throw on invalid label
            position(labels[i]);

            String label = labels[i];

            while (!uniqueLabels.add(label)) {
                label = label + "_";
            }

            selectedLabels[i] = label;
        }

        return Index.withLabels(selectedLabels);
    }

    public Index dropLabels(String... labels) {

        if (labels.length == 0) {
            return this;
        }

        List<String> toDrop = new ArrayList<>(labels.length);
        for (String l : labels) {
            if (hasLabel(l)) {
                toDrop.add(l);
            }
        }

        if (toDrop.isEmpty()) {
            return this;
        }

        String[] toKeep = new String[size() - toDrop.size()];
        for (int i = 0, j = 0; i < this.labels.length; i++) {

            if (!toDrop.contains(this.labels[i])) {
                toKeep[j] = this.labels[i];
                j++;
            }
        }

        return Index.withLabels(toKeep);
    }

    public String[] getLabels() {
        return labels;
    }

    public String getLabel(int pos) {
        return labels[pos];
    }

    public int size() {
        return labels.length;
    }

    public int position(String label) {
        if (labelPositions == null) {
            this.labelPositions = computeLabelPositions();
        }

        Integer pos = labelPositions.get(label);
        if (pos == null) {
            throw new IllegalArgumentException("Label '" + label + "' is not present in the Index");
        }

        return pos;
    }

    public boolean hasLabel(String label) {
        if (labelPositions == null) {
            this.labelPositions = computeLabelPositions();
        }

        return labelPositions.containsKey(label);
    }

    private Map<String, Integer> computeLabelPositions() {

        Map<String, Integer> index = new LinkedHashMap<>();

        for (int i = 0; i < labels.length; i++) {
            Integer previous = index.put(labels[i], i);
            if (previous != null) {
                throw new IllegalStateException("Duplicate label '"
                        + labels[i]
                        + "'. Found at " + previous + " and " + i);
            }
        }
        return index;
    }
}
