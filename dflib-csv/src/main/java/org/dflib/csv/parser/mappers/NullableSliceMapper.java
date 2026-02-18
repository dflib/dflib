package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;

import java.util.function.Function;

class NullableSliceMapper implements Function<DataSlice[], DataSlice> {

    public static final DataSlice NULL = DataSlice.of(new char[0]);

    final Function<DataSlice[], DataSlice> delegate;
    final char[] nullValue;

    NullableSliceMapper(Function<DataSlice[], DataSlice> sliceMapper, char[] nullValue) {
        this.delegate = sliceMapper;
        this.nullValue = nullValue;
    }

    @Override
    public DataSlice apply(DataSlice[] dataSlices) {
        DataSlice slice = delegate.apply(dataSlices);
        if (checkNull(slice)) {
            return NULL;
        }
        return slice;
    }

    protected boolean checkNull(DataSlice slice) {
        int from = slice.from();
        int length = slice.to() - from;
        if (nullValue == null) {
            return length == 0;
        }
        if (length != nullValue.length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            if (slice.data(from + i) != nullValue[i]) {
                return false;
            }
        }
        return true;
    }
}
