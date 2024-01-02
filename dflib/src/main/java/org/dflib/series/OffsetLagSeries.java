package org.dflib.series;

import org.dflib.Series;

import java.util.Arrays;
import java.util.Objects;

/**
 * @since 0.9
 */
// While this can be expressed as a concatenation of a RangeSeries and SingleValueSeries, a dedicated
// offset series would yield better performance
public class OffsetLagSeries<T> extends OffsetSeries<T> {

    private final int offset;

    public OffsetLagSeries(Series<T> delegate, int offset, T filler) {
        super(delegate, filler);

        if (offset >= 0) {
            throw new IllegalArgumentException("Expected negative offset: " + offset);
        }

        this.offset = offset;
    }

    @Override
    public Series<T> shift(int offset, T filler) {
        // optimize shift by unwrapping the delegate where possible
        return (offset < 0 && Objects.equals(filler, this.filler))
                ? new OffsetLagSeries<>(delegate, this.offset + offset, filler)
                : super.shift(offset, filler);
    }

    @Override
    public T get(int index) {
        int size = size();

        if (index >= size || index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return index < size + offset ? delegate.get(index - offset) : filler;
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        int size = size();

        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        int len1 = Math.min(size + offset - fromOffset, len);
        int len2 = len - len1;
        int off1 = fromOffset - offset;

        if (len1 > 0) {
            delegate.copyTo(to, off1, toOffset, len1);
        }

        if (len2 > 0) {
            Arrays.fill(to, toOffset + len1, toOffset + len, filler);
        }
    }

    @Override
    public Series<T> materialize() {

        int size = size();
        int splitPoint = size + offset;
        Object[] buffer = new Object[size];

        delegate.copyTo(buffer, -offset, 0, splitPoint);

        if (filler != null) {
            Arrays.fill(buffer, splitPoint, size, filler);
        }

        return new ArraySeries<>((T[]) buffer);
    }
}
