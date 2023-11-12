package com.nhl.dflib;

import com.nhl.dflib.concat.HConcat;
import com.nhl.dflib.range.Range;
import com.nhl.dflib.sample.Sampler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * An "index" of the DataFrame that provides access to column (and in the future potentially row) metadata.
 */
public class Index implements Iterable<String> {

    protected final String[] labels;
    protected volatile Map<String, Integer> labelPositions;

    protected Index(String... labels) {
        this.labels = labels;
    }

    /**
     * @deprecated in favor of {@link #of(Class)}
     */
    @Deprecated(since = "0.19", forRemoval = true)
    public static <E extends Enum<E>> Index forLabels(Class<E> columns) {
        return of(columns);
    }

    /**
     * Creates a new index based on enum values.
     *
     * @param columns enum type that defines Index columns
     * @return a new Index with columns matching the provided Enum
     * @since 0.19
     */
    public static <E extends Enum<E>> Index of(Class<E> columns) {

        E[] enumValues = columns.getEnumConstants();
        String[] labels = new String[enumValues.length];
        for (int i = 0; i < enumValues.length; i++) {
            labels[i] = enumValues[i].name();
        }

        return of(labels);
    }

    /**
     * @deprecated in favor of {@link #of(String...)}
     */
    @Deprecated(since = "0.19", forRemoval = true)
    public static Index forLabels(String... labels) {
        return of(labels);
    }

    /**
     * @since 0.19
     */
    public static Index of(String... labels) {
        return new Index(labels);
    }

    /**
     * @since 0.7
     * @deprecated in favor of {@link #of(Series)}
     */
    @Deprecated(since = "0.19", forRemoval = true)
    public static Index forLabels(Series<String> labels) {
        return of(labels);
    }

    /**
     * @since 0.19
     */
    public static Index of(Series<String> labels) {
        return new Index(labels.toArray(new String[0]));
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #ofDeduplicated(String...)}
     */
    @Deprecated(since = "0.19", forRemoval = true)
    public static Index forLabelsDeduplicate(String... labels) {
        return ofDeduplicated(labels);
    }

    /**
     * Creates an index from an array of labels. Duplicate labels will be renamed by appending one or more underscore
     * symbols.
     *
     * @since 0.6
     */
    public static Index ofDeduplicated(String... labels) {
        int len = labels.length;
        String[] selectedLabels = new String[len];
        Set<String> uniqueLabels = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {

            String label = labels[i];

            while (!uniqueLabels.add(label)) {
                label = label + "_";
            }

            selectedLabels[i] = label;
        }

        return new Index(selectedLabels);
    }

    @Override
    public Iterator<String> iterator() {
        return new ArrayIterator<>(labels);
    }

    /**
     * Renames index labels by applying the provided function to each label. Useful for name conversions like
     * lower-casing, etc.
     *
     * @param renameFunction a function that is passed each label in turn
     * @return a new Index with renamed labels
     * @since 0.6
     */
    public Index rename(UnaryOperator<String> renameFunction) {

        int len = size();

        String[] newLabels = new String[len];
        for (int i = 0; i < len; i++) {
            newLabels[i] = renameFunction.apply(labels[i]);
        }

        return new Index(newLabels);
    }

    public Index rename(Map<String, String> oldToNewLabels) {

        int len = size();

        Set<String> unique = new LinkedHashSet<>();

        for (int i = 0; i < len; i++) {
            String oldLabel = labels[i];
            String newLabel = oldToNewLabels.get(oldLabel);
            String label = newLabel != null ? newLabel : oldLabel;
            if (!unique.add(label)) {
                throw new IllegalArgumentException("Duplicate column name: " + label);
            }
        }

        return new Index(unique.toArray(new String[0]));
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
        // TODO: implement direct array copy instead of wrapping in Index first
        return HConcat.zipIndex(this, of(extraLabels));
    }

    /**
     * @since 0.7
     */
    public Index selectPositions(IntSeries positions) {
        int len = positions.size();
        String[] selectedLabels = new String[len];
        Set<String> uniqueLabels = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {

            String label = labels[positions.getInt(i)];

            while (!uniqueLabels.add(label)) {
                label = label + "_";
            }

            selectedLabels[i] = label;
        }

        return Index.of(selectedLabels);
    }

    public Index selectPositions(int... positions) {
        int len = positions.length;
        String[] selectedLabels = new String[len];
        Set<String> uniqueLabels = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {

            String label = labels[positions[i]];

            while (!uniqueLabels.add(label)) {
                label = label + "_";
            }

            selectedLabels[i] = label;
        }

        return Index.of(selectedLabels);
    }

    /**
     * @param fromInclusive position of the first label from this Index to include in the new Index.
     * @param toExclusive   position of the label from this Index following the last included position.
     * @return a new index with labels from this index in the range specified
     * @since 0.6
     */
    // TODO: align naming with "select*" methods. Perhaps a Range object is required
    public Index rangeOpenClosed(int fromInclusive, int toExclusive) {

        int len = toExclusive - fromInclusive;
        Range.checkRange(fromInclusive, len, labels.length);

        String[] newLabels = new String[len];
        System.arraycopy(labels, fromInclusive, newLabels, 0, len);

        return Index.of(newLabels);
    }

    public Index selectLabels(String... labels) {

        // check whether the labels are valid. "position" call on an invalid label would throw
        int len = labels.length;
        for (int i = 0; i < len; i++) {
            position(labels[i]);
        }

        return Index.ofDeduplicated(labels);
    }

    /**
     * @since 0.7
     */
    public Index selectLabels(Predicate<String> includeCondition) {
        // must preserve label order
        Set<String> selected = new LinkedHashSet<>();

        int len = labels.length;
        for (int i = 0; i < len; i++) {
            if (includeCondition.test(labels[i])) {
                selected.add(labels[i]);
            }
        }

        return selected.size() == size() ? this : Index.of(selected.toArray(new String[0]));
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

        return Index.of(toKeep);
    }

    /**
     * @since 0.7
     */
    public Index dropLabels(Predicate<String> labelCondition) {
        // must preserve label order
        Set<String> selected = new LinkedHashSet<>();

        int len = labels.length;
        for (int i = 0; i < len; i++) {
            if (!labelCondition.test(labels[i])) {
                selected.add(labels[i]);
            }
        }

        return selected.size() == size() ? this : Index.of(selected.toArray(new String[0]));
    }

    public String[] getLabels() {
        return labels;
    }

    /**
     * @since 0.19
     */
    public int[] getPositions() {
        int len = labels.length;
        int[] positions = new int[len];

        for (int i = 0; i < len; i++) {
            positions[i] = i;
        }

        return positions;
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

    /**
     * Returns Index positions corresponding to the array of labels.
     *
     * @since 0.19
     */
    public int[] positions(String... labels) {

        int len = labels.length;
        if (len == 0) {
            return new int[0];
        }

        if (labelPositions == null) {
            this.labelPositions = computeLabelPositions();
        }

        int[] positions = new int[len];

        for (int i = 0; i < len; i++) {
            Integer pos = labelPositions.get(labels[i]);
            if (pos == null) {
                throw new IllegalArgumentException("Label '" + labels[i] + "' is not present in the Index");
            }

            positions[i] = pos;
        }

        return positions;
    }

    public boolean hasLabel(String label) {
        if (labelPositions == null) {
            this.labelPositions = computeLabelPositions();
        }

        return labelPositions.containsKey(label);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj instanceof Index) {
            Index otherIndex = (Index) obj;
            return Arrays.equals(labels, otherIndex.labels);
        }

        return false;
    }

    /**
     * @since 0.7
     */
    public Index sample(int size) {
        return selectPositions(Sampler.sampleIndex(size, size()));
    }

    /**
     * @since 0.7
     */
    public Index sample(int size, Random random) {
        return selectPositions(Sampler.sampleIndex(size, size(), random));
    }

    /**
     * @return a new Series object with Index labels as elements.
     * @since 0.7
     */
    public Series<String> toSeries() {
        return Series.of(labels);
    }

    @Override
    public String toString() {
        return toSeries().toString();
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

    static class ArrayIterator<T> implements Iterator<T> {

        private final int len;
        private final T[] data;
        private int counter;

        public ArrayIterator(T[] data) {
            this.data = data;
            this.len = data.length;
        }

        @Override
        public boolean hasNext() {
            return counter < len;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Past the end of the iterator");
            }

            return data[counter++];
        }
    }

}
