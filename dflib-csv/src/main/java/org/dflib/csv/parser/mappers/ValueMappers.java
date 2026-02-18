package org.dflib.csv.parser.mappers;

import org.dflib.BoolValueMapper;
import org.dflib.DoubleValueMapper;
import org.dflib.FloatValueMapper;
import org.dflib.IntValueMapper;
import org.dflib.LongValueMapper;
import org.dflib.ValueMapper;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnFormat;

import java.util.function.Function;

class ValueMappers {

    private ValueMappers() {
    }

    // Primitive mappers (one per primitive type)
    static BoolValueMapper<DataSlice[]> forBool(Function<DataSlice[], DataSlice> sliceMapper,
                                                       CsvColumnFormat columnFormat) {
        if (!columnFormat.nullable()) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                return !s.empty() && BoolParser.parse(s);
            };
        }
        boolean defaultValue = defaultValue(columnFormat);
        return row -> {
            DataSlice s = sliceMapper.apply(row);
            if (s.empty()) return defaultValue;
            return isNull(s) ? defaultValue : BoolParser.parse(s);
        };
    }

    static IntValueMapper<DataSlice[]> forInt(Function<DataSlice[], DataSlice> sliceMapper,
                                                     CsvColumnFormat columnFormat) {
        if (!columnFormat.nullable()) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                return s.empty() ? 0 : IntParser.parse(s);
            };
        }
        int defaultValue = defaultValue(columnFormat);
        return row -> {
            DataSlice s = sliceMapper.apply(row);
            if (s.empty()) return defaultValue;
            return isNull(s) ? defaultValue : IntParser.parse(s);
        };
    }

    static LongValueMapper<DataSlice[]> forLong(Function<DataSlice[], DataSlice> sliceMapper,
                                                       CsvColumnFormat columnFormat) {
        if (!columnFormat.nullable()) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                return s.empty() ? 0 : LongParser.parse(s);
            };
        }
        long defaultValue = defaultValue(columnFormat);
        return row -> {
            DataSlice s = sliceMapper.apply(row);
            if (s.empty()) return defaultValue;
            return isNull(s) ? defaultValue : LongParser.parse(s);
        };
    }

    static FloatValueMapper<DataSlice[]> forFloat(Function<DataSlice[], DataSlice> sliceMapper,
                                                         CsvColumnFormat columnFormat) {
        if (!columnFormat.nullable()) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                return s.empty() ? 0 : (float) DoubleParser.parse(s);
            };
        }
        float defaultValue = defaultValue(columnFormat);
        return row -> {
            DataSlice s = sliceMapper.apply(row);
            if (s.empty()) return defaultValue;
            return isNull(s) ? defaultValue : (float) DoubleParser.parse(s);
        };
    }

    static DoubleValueMapper<DataSlice[]> forDouble(Function<DataSlice[], DataSlice> sliceMapper,
                                                           CsvColumnFormat columnFormat) {
        if (!columnFormat.nullable()) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                return s.empty() ? 0 : DoubleParser.parse(s);
            };
        }
        double defaultValue = defaultValue(columnFormat);
        return row -> {
            DataSlice s = sliceMapper.apply(row);
            if (s.empty()) return defaultValue;
            return isNull(s) ? defaultValue : DoubleParser.parse(s);
        };
    }

    // Object mapper (generic, covers String/BigInteger/BigDecimal/OTHER)
    static <T> ValueMapper<DataSlice[], T> forObject(
            Function<DataSlice[], DataSlice> sliceMapper,
            Function<DataSlice, T> valueMapper,
            CsvColumnFormat columnFormat) {
        if (!columnFormat.nullable()) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                return s.empty() ? null : valueMapper.apply(s);
            };
        }

        T defaultValue = defaultValue(columnFormat, false);

        return row -> {
            DataSlice s = sliceMapper.apply(row);
            if (s.empty()) return defaultValue;
            return isNull(s) ? defaultValue : valueMapper.apply(s);
        };
    }

    @SuppressWarnings("unchecked")
    private static <T> T defaultValue(CsvColumnFormat columnFormat, boolean mandatory) {
        if (mandatory && columnFormat.defaultValue() == null) {
            throw new IllegalArgumentException("No default value is set for the nullable column `" + columnFormat.name() + "`");
        }
        return (T) columnFormat.defaultValue();
    }

    private static <T> T defaultValue(CsvColumnFormat columnFormat) {
        return defaultValue(columnFormat, true);
    }

    private static boolean isNull(DataSlice slice) {
        return slice == NullableSliceMapper.NULL;
    }
}