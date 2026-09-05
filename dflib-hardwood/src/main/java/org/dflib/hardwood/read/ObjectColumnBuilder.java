package org.dflib.hardwood.read;

import dev.hardwood.row.StructAccessor;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ValueCompactor;

/**
 * A {@link ColumnBuilder} accumulating boxed values.
 *
 * @since 2.0.0
 */
public class ObjectColumnBuilder<T> implements ColumnBuilder {

    private final ValueReader<T> reader;
    private final ObjectAccum<T> accum;
    private final ValueCompactor<T> compactor;

    /**
     * @param compact whether repeated values should be replaced with a single shared instance. Must be false for
     *                values that are mutable or lack value-based equality, such as byte arrays and collections.
     */
    public static <T> ObjectColumnBuilder<T> of(int capacity, boolean compact, ValueReader<T> reader) {
        return new ObjectColumnBuilder<>(reader, capacity, compact ? new ValueCompactor<>() : null);
    }

    protected ObjectColumnBuilder(ValueReader<T> reader, int capacity, ValueCompactor<T> compactor) {
        this.reader = reader;
        this.accum = new ObjectAccum<>(capacity);
        this.compactor = compactor;
    }

    @Override
    public void append(StructAccessor row, int fieldIndex) {
        T v = reader.read(row, fieldIndex);
        accum.push(compactor != null ? compactor.get(v) : v);
    }

    @Override
    public Series<?> toSeries() {
        return accum.toSeries();
    }
}
