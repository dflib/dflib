package org.dflib.series;

import org.dflib.Series;
import org.dflib.collection.JavaArrays;

import java.util.Arrays;
import java.util.Objects;

// While this can be expressed as a concatenation of a RangeSeries and SingleValueSeries, a dedicated
// offset series would yield better performance
public class OffsetLeadSeries<T> extends OffsetSeries<T> {

    private final int offset;

    public OffsetLeadSeries(Series<T> delegate, int offset, T filler) {
        super(delegate, filler);

        if (offset <= 0) {
            throw new IllegalArgumentException("Expected positive offset: " + offset);
        }

        this.offset = offset;
    }

    @Override
    public Series<T> shift(int offset, T filler) {
        // optimize shift by unwrapping the delegate where possible
        return (offset > 0 && Objects.equals(filler, this.filler))
                ? new OffsetLeadSeries<>(delegate, this.offset + offset, filler)
                : super.shift(offset, filler);
    }

    @Override
    public T get(int index) {
        int size = size();

        if (index >= size || index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        return index < offset ? filler : delegate.get(index - offset);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        int size = size();

        if (fromOffset + len > size) {
            throw new ArrayIndexOutOfBoundsException(fromOffset + len);
        }

        int len1 = Math.max(0, Math.min(offset - fromOffset, len));
        int len2 = len - len1;
        int off2 = fromOffset < offset ? 0 : fromOffset - offset;

        if (len1 > 0) {
            Arrays.fill(to, toOffset, toOffset + len1, filler);
        }

        if (len2 > 0) {
            delegate.copyTo(to, off2, toOffset + len1, len2);
        }
    }

    @Override
    public Series<T> materialize() {

        int size = size();
        T[] data = JavaArrays.newArray(nominalType, size);

        if (filler != null) {
            Arrays.fill(data, 0, offset, filler);
        }

        delegate.copyTo(data, 0, offset, size - offset);

        return new ArraySeries<>(data);
    }
}
