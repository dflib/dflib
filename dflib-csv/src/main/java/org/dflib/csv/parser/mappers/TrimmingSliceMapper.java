package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.Trim;

import java.util.function.Function;

class TrimmingSliceMapper implements Function<DataSlice[], DataSlice> {

    Function<DataSlice[], DataSlice> sliceMapper;
    Trim trim;

    TrimmingSliceMapper(Function<DataSlice[], DataSlice> sliceMapper, Trim trim) {
        this.sliceMapper = sliceMapper;
        this.trim = trim;
    }

    @Override
    public DataSlice apply(DataSlice[] dataSlices) {
        DataSlice slice = sliceMapper.apply(dataSlices);
        if (!slice.quoted()) {
            normalizeFrom(slice, trim);
            normalizeTo(slice, trim);
        }
        return slice;
    }

    protected static void normalizeFrom(DataSlice slice, Trim trim) {
        if (trim == Trim.LEFT || trim == Trim.FULL) {
            int from = slice.from();
            // Trim leading whitespace (use exclusive 'to' bound)
            while (from < slice.to() && Character.isWhitespace(slice.data(from))) {
                from++;
            }
            slice.setFrom(from);
        }
    }

    protected static void normalizeTo(DataSlice slice, Trim trim) {
        if (trim == Trim.RIGHT || trim == Trim.FULL) {
            int to = slice.to();
            // Trim trailing whitespace (treat 'to' as exclusive bound)
            while (to > slice.from() && Character.isWhitespace(slice.data(to - 1))) {
                to--;
            }
            slice.setTo(to);
        }
    }
}
