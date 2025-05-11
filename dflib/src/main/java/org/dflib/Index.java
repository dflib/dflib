package org.dflib;

import org.dflib.index.StringDeduplicator;
import org.dflib.range.Range;
import org.dflib.sample.Sampler;

import java.util.ArrayList;
import java.util.Arrays;
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
 * A Series-like object with access to column names and positions. Index is an ordered sequence of Strings, with
 * position resolution by value implemented as amortized O(1) operations (i.e. {@link #contains(String)} and
 * {@link #position(String)}). Values are generally unique. Normally, Index is used as DataFrame header, with values
 * corresponding to the column names.
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
     */
    public static <E extends Enum<E>> Index of(Class<E> columns) {

        E[] enumValues = columns.getEnumConstants();
        String[] values = new String[enumValues.length];
        for (int i = 0; i < enumValues.length; i++) {
            values[i] = enumValues[i].name();
        }

        return of(values);
    }


    public static Index of(String... values) {
        return new Index(values);
    }


    public static Index of(Series<String> values) {
        return new Index(values.toArray(new String[0]));
    }

    /**
     * Creates an index from an array of values. Duplicate values will be renamed by appending one or more underscore
     * characters to ensure uniqueness.
     */
    public static Index ofDeduplicated(String... values) {
        String[] nonConflicted = StringDeduplicator.of(values.length).nonConflicting(values);
        return new Index(nonConflicted);
    }

    @Override
    public Iterator<String> iterator() {
        return new ArrayIterator<>(values);
    }

    /**
     * Replaces Index values by applying the provided function to each value. Useful for name conversions like
     * lower-casing, etc.
     *
     * @param mapper a function that is passed each value in turn, altering it in some way
     * @return a new Index with renamed values
     */
    public Index replace(UnaryOperator<String> mapper) {

        int len = size();

        String[] newVals = new String[len];
        for (int i = 0; i < len; i++) {
            newVals[i] = mapper.apply(values[i]);
        }

        return new Index(newVals);
    }

    public Index replace(Map<String, String> oldToNewValues) {

        int len = size();
        LinkedHashSet<String> unique = new LinkedHashSet<>((int) (len / 0.75));
        String[] replaced = new String[len];

        for (int i = 0; i < len; i++) {

            // TODO: "val" may be null if it is mapped to null in the renaming map. Though that's not the only place
            //  where we allow nulls to be passed as column names, so I guess that's ok?
            String val = oldToNewValues.getOrDefault(values[i], values[i]);

            if (!unique.add(val)) {
                throw new IllegalArgumentException("Duplicate column name: " + val);
            }

            replaced[i] = val;
        }

        return new Index(replaced);
    }

    /**
     * Extends the index, adding extra values to it. The newly added values are deduplicated using "_" suffix to
     * make sure they do not conflict with the existing values and with each other.
     */
    public Index expand(String... values) {
        int rlen = values.length;
        if (rlen == 0) {
            return this;
        }

        int llen = this.values.length;

        String[] expanded = new String[llen + rlen];
        System.arraycopy(this.values, 0, expanded, 0, llen);
        System.arraycopy(values, 0, expanded, llen, rlen);

        return Index.ofDeduplicated(expanded);
    }

    public Index select(IntSeries positions) {
        int len = positions.size();
        String[] selected = new String[len];
        Set<String> unique = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {

            String val = values[positions.getInt(i)];

            while (!unique.add(val)) {
                val = val + "_";
            }

            selected[i] = val;
        }

        return Index.of(selected);
    }

    public Index select(int... positions) {
        int len = positions.length;
        String[] selected = new String[len];
        Set<String> unique = new HashSet<>((int) (len / 0.75));

        for (int i = 0; i < len; i++) {

            String val = values[positions[i]];

            while (!unique.add(val)) {
                val = val + "_";
            }

            selected[i] = val;
        }

        return Index.of(selected);
    }

    /**
     * @param fromInclusive position of the first value from this Index to include in the new Index.
     * @param toExclusive   position of the value from this Index following the last included position.
     * @return a new index with values from this Index in the range specified
     */
    public Index selectRange(int fromInclusive, int toExclusive) {

        int len = toExclusive - fromInclusive;
        Range.checkRange(fromInclusive, len, values.length);

        String[] newValues = new String[len];
        System.arraycopy(values, fromInclusive, newValues, 0, len);

        return Index.of(newValues);
    }

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

    public Index selectExcept(String... values) {

        if (values.length == 0) {
            return this;
        }

        List<String> toDrop = new ArrayList<>(values.length);
        for (String v : values) {
            if (contains(v)) {
                toDrop.add(v);
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
     * Returns an Index with the first <code>len</code> elements of this Index. If this Index is shorter than the
     * requested length, then the entire Index is returned. If <code>len</code> is negative, instead of returning the
     * leading elements, they are skipped, and the rest of the Index is returned.
     *
     * @since 2.0.0
     */
    public Index head(int len) {
        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? tail(size() + len) : selectRange(0, len);
    }

    /**
     * Returns an Index with the last <code>len</code> elements of this Index. If this Index is shorter than the
     * requested length, then the entire Index is returned. If <code>len</code> is negative, instead of returning the
     * trailing elements, they are skipped, and the rest of the Index is returned.
     *
     * @since 2.0.0
     */
    public Index tail(int len) {
        int size = size();

        if (Math.abs(len) >= size()) {
            return this;
        }

        return len < 0 ? head(size + len) : selectRange(size - len, size);
    }

    // not-public direct accessor to mutable values ... Saves on data copy, but the callers should be
    // careful not to alter the values
    String[] toArrayNoCopy() {
        return values;
    }

    public String[] toArray() {
        int len = values.length;
        String[] copy = new String[len];
        System.arraycopy(values, 0, copy, 0, len);
        return copy;
    }

    /**
     * Returns a String value at the given position.
     */
    public String get(int pos) {
        return values[pos];
    }

    public int size() {
        return values.length;
    }

    public int position(String value) {
        if (valueIndex == null) {
            this.valueIndex = computeIndex();
        }

        Integer pos = valueIndex.get(value);
        if (pos == null) {
            throw new IllegalArgumentException("Value '" + value + "' is not present in the Index");
        }

        return pos;
    }

    /**
     * Returns Index positions corresponding to the array of values
     */
    public int[] positions(String... value) {

        int len = value.length;
        if (len == 0) {
            return new int[0];
        }

        if (valueIndex == null) {
            this.valueIndex = computeIndex();
        }

        int[] positions = new int[len];

        for (int i = 0; i < len; i++) {
            Integer pos = valueIndex.get(value[i]);
            if (pos == null) {
                throw new IllegalArgumentException("Value '" + value[i] + "' is not present in the Index");
            }

            positions[i] = pos;
        }

        return positions;
    }

    /**
     * Returns Index positions for all index values except the ones specified as the argument
     */
    public int[] positionsExcept(String... exceptVals) {

        int len = exceptVals.length;
        if (len == 0) {
            return positionsArray(values.length);
        }

        Set<String> excludes = new HashSet<>();
        for (String e : exceptVals) {
            excludes.add(e);
        }

        return positions(s -> !excludes.contains(s));
    }


    /**
     * Returns Index positions for all Index values except the positions specified as the argument
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


    public int[] positions(Predicate<String> condition) {
        if (valueIndex == null) {
            this.valueIndex = computeIndex();
        }

        List<Integer> positions = new ArrayList<>();
        for (Map.Entry<String, Integer> e : valueIndex.entrySet()) {
            if (condition.test(e.getKey())) {
                positions.add(e.getValue());
            }
        }

        // presumably "positions" is in order of values, so no need to sort "positions"
        int len = positions.size();
        int[] intPositions = new int[len];
        for (int i = 0; i < len; i++) {
            intPositions[i] = positions.get(i);
        }

        return intPositions;
    }

    public boolean contains(String value) {
        if (valueIndex == null) {
            this.valueIndex = computeIndex();
        }

        return valueIndex.containsKey(value);
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

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }


    public Index sample(int size) {
        return select(Sampler.sampleIndex(size, size()));
    }


    public Index sample(int size, Random random) {
        return select(Sampler.sampleIndex(size, size(), random));
    }

    /**
     * @return a Series object with Index values as elements.
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
                throw new IllegalStateException("Duplicate value '"
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
