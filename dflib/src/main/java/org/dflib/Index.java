package org.dflib;

import org.dflib.index.LabelDeduplicator;
import org.dflib.range.Range;
import org.dflib.sample.Sampler;

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
 * DataFrame header with access to column names and positions. While not formally a {@link Series}, the Index object is
 * a very similar data structure and has a similar API. It is an ordered sequence of Strings, but the positions of
 * its values are indexed (so {@link #contains(String)} and {@link #position(String)} are O(1) operations), and values
 * are generally unique.
 */
public class Index implements Iterable<String> {

    protected final String[] values;
    protected volatile Map<String, Integer> valueIndex;

    protected Index(String... values) {
        this.values = values;
    }

    /**
     * Creates a new index based on enum values.
     *
     * @param columns enum type that defines Index columns
     * @return a new Index with columns matching the provided Enum
     * @since 1.0.0-M19
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
     * @since 1.0.0-M19
     */
    public static Index of(String... labels) {
        return new Index(labels);
    }

    /**
     * @since 1.0.0-M19
     */
    public static Index of(Series<String> labels) {
        return new Index(labels.toArray(new String[0]));
    }

    /**
     * Creates an index from an array of labels. Duplicate labels will be renamed by appending one or more underscore
     * characters to ensure uniqueness.
     *
     * @since 1.0.0-M19
     */
    public static Index ofDeduplicated(String... labels) {
        String[] selectedLabels = LabelDeduplicator.of(labels.length).nonConflictingLabels(labels);
        return new Index(selectedLabels);
    }

    @Override
    public Iterator<String> iterator() {
        return new ArrayIterator<>(values);
    }

    /**
     * Renames index labels by applying the provided function to each label. Useful for name conversions like
     * lower-casing, etc.
     *
     * @param mapper a function that is passed each value in turn, altering it in some way
     * @return a new Index with renamed labels
     * @since 0.6
     */
    public Index replace(UnaryOperator<String> mapper) {

        int len = size();

        String[] newVals = new String[len];
        for (int i = 0; i < len; i++) {
            newVals[i] = mapper.apply(values[i]);
        }

        return new Index(newVals);
    }

    /**
     * @since 0.6
     * @deprecated in favor of {@link #replace(UnaryOperator)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index rename(UnaryOperator<String> renamer) {
        return replace(renamer);
    }

    /**
     * @since 1.0.0-M21
     */
    public Index replace(Map<String, String> oldToNewValues) {

        int len = size();

        Set<String> unique = new LinkedHashSet<>();

        for (int i = 0; i < len; i++) {
            String oldLabel = values[i];
            String newLabel = oldToNewValues.get(oldLabel);
            String label = newLabel != null ? newLabel : oldLabel;
            if (!unique.add(label)) {
                throw new IllegalArgumentException("Duplicate column name: " + label);
            }
        }

        return new Index(unique.toArray(new String[0]));
    }

    /**
     * @deprecated in favor of {@link #replace(Map)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index rename(Map<String, String> oldToNewValues) {
        return replace(oldToNewValues);
    }

    /**
     * @deprecated not particularly useful, as this is a functional equivalent of {@link #of(String...)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index rename(String... newLabels) {

        if (newLabels.length != size()) {
            throw new IllegalArgumentException("New columns length does not match existing index size: "
                    + newLabels.length + " vs " + size());
        }

        Map<String, String> map = new HashMap<>((int) (newLabels.length / 0.75));
        for (int i = 0; i < newLabels.length; i++) {
            map.put(values[i], newLabels[i]);
        }

        return replace(map);
    }

    /**
     * @since 1.0.0-M21
     */
    public Index add(String... values) {
        int rlen = values.length;
        int llen = this.values.length;

        String[] zipped = new String[llen + rlen];
        System.arraycopy(this.values, 0, zipped, 0, llen);

        // resolve dupes on the right
        for (int i = 0; i < rlen; i++) {

            String name = values[i];
            while (contains(name)) {
                name = name + "_";
            }

            int ri = i + llen;
            zipped[ri] = name;
        }

        return Index.of(zipped);
    }

    /**
     * @deprecated in favor of {@link #add(String...)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index addLabels(String... extraLabels) {
        return add(extraLabels);
    }

    /**
     * @since 1.0.0-M21
     */
    public Index select(IntSeries positions) {
        int len = positions.size();
        String[] selectedLabels = new String[len];
        Set<String> uniqueLabels = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {

            String label = values[positions.getInt(i)];

            while (!uniqueLabels.add(label)) {
                label = label + "_";
            }

            selectedLabels[i] = label;
        }

        return Index.of(selectedLabels);
    }

    /**
     * @since 0.7
     * @deprecated in favor of {@link #select(IntSeries)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index selectPositions(IntSeries positions) {
        return select(positions);
    }

    /**
     * @since 1.0.0-M21
     */
    public Index select(int... positions) {
        int len = positions.length;
        String[] selectedLabels = new String[len];
        Set<String> uniqueLabels = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {

            String label = values[positions[i]];

            while (!uniqueLabels.add(label)) {
                label = label + "_";
            }

            selectedLabels[i] = label;
        }

        return Index.of(selectedLabels);
    }

    /**
     * @deprecated in favor of {@link #select(int...)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index selectPositions(int... positions) {
        return select(positions);
    }

    /**
     * @param fromInclusive position of the first label from this Index to include in the new Index.
     * @param toExclusive   position of the label from this Index following the last included position.
     * @return a new index with labels from this index in the range specified
     * @since 1.0.0-M19
     */
    public Index selectRange(int fromInclusive, int toExclusive) {

        int len = toExclusive - fromInclusive;
        Range.checkRange(fromInclusive, len, values.length);

        String[] newLabels = new String[len];
        System.arraycopy(values, fromInclusive, newLabels, 0, len);

        return Index.of(newLabels);
    }

    /**
     * @deprecated not particularly useful, as this is a functional equivalent of {@link #of(String...)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index selectLabels(String... labels) {

        // check whether the labels are valid. "position" call on an invalid label would throw
        int len = labels.length;
        for (int i = 0; i < len; i++) {
            position(labels[i]);
        }

        return Index.ofDeduplicated(labels);
    }

    /**
     * @since 1.0.0-M21
     */
    public Index select(Predicate<String> condition) {
        int len = values.length;
        List<String> selected = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            if (condition.test(values[i])) {
                selected.add(values[i]);
            }
        }

        return selected.size() == size() ? this : Index.of(selected.toArray(new String[0]));
    }

    /**
     * @since 0.7
     * @deprecated in favor of {@link #select(Predicate)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index selectLabels(Predicate<String> includeCondition) {
        return select(includeCondition);
    }

    /**
     * @since 1.0.0-M21
     */
    public Index selectExcept(String... values) {

        if (values.length == 0) {
            return this;
        }

        List<String> toDrop = new ArrayList<>(values.length);
        for (String l : values) {
            if (contains(l)) {
                toDrop.add(l);
            }
        }

        if (toDrop.isEmpty()) {
            return this;
        }

        String[] toKeep = new String[size() - toDrop.size()];
        for (int i = 0, j = 0; i < this.values.length; i++) {

            if (!toDrop.contains(this.values[i])) {
                toKeep[j] = this.values[i];
                j++;
            }
        }

        return Index.of(toKeep);
    }

    /**
     * @deprecated in favor of {@link #selectExcept(String...)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index dropLabels(String... labels) {
        return selectExcept(labels);
    }

    /**
     * @since 1.0.0-M21
     */
    public Index selectExcept(Predicate<String> condition) {

        int len = values.length;
        List<String> selected = new ArrayList<>(len);

        for (int i = 0; i < len; i++) {
            if (!condition.test(values[i])) {
                selected.add(values[i]);
            }
        }

        return selected.size() == size() ? this : Index.of(selected.toArray(new String[0]));
    }

    /**
     * @deprecated in favor of {@link #selectExcept(Predicate)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public Index dropLabels(Predicate<String> labelCondition) {
        return selectExcept(labelCondition);
    }

    /**
     * @deprecated in favor of {@link #toArray()}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public String[] getLabels() {
        return toArray();
    }

    // not-public direct accessor to mutable labels ... Saves on data copy, but the callers should be
    // careful not to alter the values
    String[] toArrayNoCopy() {
        return values;
    }

    /**
     * @since 1.0.0-M21
     */
    public String[] toArray() {
        int len = values.length;
        String[] copy = new String[len];
        System.arraycopy(values, 0, copy, 0, len);
        return copy;
    }

    /**
     * Returns a column label at the given position.
     *
     * @since 1.0.0-M21
     */
    public String get(int pos) {
        return values[pos];
    }

    /**
     * @deprecated in favor of {@link #get(int)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public String getLabel(int pos) {
        return get(pos);
    }

    public int size() {
        return values.length;
    }

    public int position(String label) {
        if (valueIndex == null) {
            this.valueIndex = computeIndex();
        }

        Integer pos = valueIndex.get(label);
        if (pos == null) {
            throw new IllegalArgumentException("Label '" + label + "' is not present in the Index");
        }

        return pos;
    }

    /**
     * Returns Index positions corresponding to the array of labels.
     *
     * @since 1.0.0-M19
     */
    public int[] positions(String... labels) {

        int len = labels.length;
        if (len == 0) {
            return new int[0];
        }

        if (valueIndex == null) {
            this.valueIndex = computeIndex();
        }

        int[] positions = new int[len];

        for (int i = 0; i < len; i++) {
            Integer pos = valueIndex.get(labels[i]);
            if (pos == null) {
                throw new IllegalArgumentException("Label '" + labels[i] + "' is not present in the Index");
            }

            positions[i] = pos;
        }

        return positions;
    }

    /**
     * Returns Index positions for all index labels except the labels specified as the argument
     *
     * @since 1.0.0-M19
     */
    public int[] positionsExcept(String... exceptLabels) {

        int len = exceptLabels.length;
        if (len == 0) {
            return positionsArray(values.length);
        }

        Set<String> excludes = new HashSet<>();
        for (String e : exceptLabels) {
            excludes.add(e);
        }

        return positions(s -> !excludes.contains(s));
    }


    /**
     * Returns Index positions for all index labels except the positions specified as the argument
     *
     * @since 1.0.0-M19
     */
    public int[] positionsExcept(int... exceptPositions) {

        int exceptLen = exceptPositions.length;
        if (exceptLen == 0) {
            return positionsArray(values.length);
        }

        Set<Integer> excludes = new HashSet<>((int) Math.ceil(exceptLen / 0.75));
        for (int e : exceptPositions) {
            excludes.add(e);
        }

        int len = values.length;
        int[] positions = new int[len - excludes.size()];

        for (int ii = 0, i = 0; i < len; i++) {
            if (!excludes.contains(i)) {
                positions[ii++] = i;
            }
        }

        return positions;
    }

    /**
     * @since 1.0.0-M19
     */
    public int[] positions(Predicate<String> labelCondition) {
        if (valueIndex == null) {
            this.valueIndex = computeIndex();
        }

        List<Integer> positions = new ArrayList<>();
        for (Map.Entry<String, Integer> e : valueIndex.entrySet()) {
            if (labelCondition.test(e.getKey())) {
                positions.add(e.getValue());
            }
        }

        // presumably "labelPositions" is in order of labels, so no need to sort "positions"
        int len = positions.size();
        int[] intPositions = new int[len];
        for (int i = 0; i < len; i++) {
            intPositions[i] = positions.get(i);
        }

        return intPositions;
    }

    /**
     * @since 1.0.0-M21
     */
    public boolean contains(String value) {
        if (valueIndex == null) {
            this.valueIndex = computeIndex();
        }

        return valueIndex.containsKey(value);
    }

    /**
     * @deprecated in favor of {@link #contains(String)}
     */
    @Deprecated(since = "1.0.0-M21", forRemoval = true)
    public boolean hasLabel(String label) {
        return contains(label);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj instanceof Index) {
            Index otherIndex = (Index) obj;
            return Arrays.equals(values, otherIndex.values);
        }

        return false;
    }

    /**
     * @since 0.7
     */
    public Index sample(int size) {
        return select(Sampler.sampleIndex(size, size()));
    }

    /**
     * @since 0.7
     */
    public Index sample(int size, Random random) {
        return select(Sampler.sampleIndex(size, size(), random));
    }

    /**
     * @return a new Series object with Index labels as elements.
     * @since 0.7
     */
    public Series<String> toSeries() {
        return Series.of(values);
    }

    @Override
    public String toString() {
        return toSeries().toString();
    }

    protected Map<String, Integer> computeIndex() {

        Map<String, Integer> index = new LinkedHashMap<>();

        for (int i = 0; i < values.length; i++) {
            Integer previous = index.put(values[i], i);
            if (previous != null) {
                throw new IllegalStateException("Duplicate label '"
                        + values[i]
                        + "'. Found at " + previous + " and " + i);
            }
        }
        return index;
    }

    static int[] positionsArray(int len) {
        int[] positions = new int[len];

        for (int i = 0; i < len; i++) {
            positions[i] = i;
        }

        return positions;
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
