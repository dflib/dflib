package org.dflib.echarts.render;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * An adapter for a list of values that simplifies stripping trailing commas when rendering a list in a Mustache template
 */
public record ValueModels<T>(List<T> values) implements Iterable<ValueModel<T>> {

    /**
     * @deprecated just use the constructor
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static <T> ValueModels<T> of(List<T> values) {
        return new ValueModels<>(values);
    }

    public ValueModels(List<T> values) {
        this.values = Objects.requireNonNull(values);
    }

    public int size() {
        return values.size();
    }

    public T getValue(int i) {
        return values.get(i);
    }

    @Override
    public Iterator<ValueModel<T>> iterator() {
        return new Iterator<>() {

            int i;
            final int len = values.size();

            @Override
            public boolean hasNext() {
                return i < len;
            }

            @Override
            public ValueModel<T> next() {
                return new ValueModel<>(values.get(i++), !hasNext());
            }
        };
    }
}
