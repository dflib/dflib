package org.dflib.csv.parser.mappers;

import org.dflib.BoolValueMapper;
import org.dflib.DoubleValueMapper;
import org.dflib.FloatValueMapper;
import org.dflib.IntValueMapper;
import org.dflib.LongValueMapper;
import org.dflib.ValueMapper;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvColumnMapping;

import java.util.function.Function;

class ValueMappers {

    private ValueMappers() {
    }

    // Primitive mappers (one per primitive type). Missing and null values require a default value
    static BoolValueMapper<DataSlice[]> forBool(Function<DataSlice[], DataSlice> sliceMapper,
                                                CsvColumnMapping columnFormat) {
        if (!hasDefault(columnFormat)) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                if (s.empty() || isNull(s)) throw noValue("boolean");
                return BoolParser.parse(s);
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
                                              CsvColumnMapping columnFormat) {
        if (!hasDefault(columnFormat)) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                if (s.empty() || isNull(s)) throw noValue("int");
                return IntParser.parse(s);
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
                                                CsvColumnMapping columnFormat) {
        if (!hasDefault(columnFormat)) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                if (s.empty() || isNull(s)) throw noValue("long");
                return LongParser.parse(s);
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
                                                  CsvColumnMapping columnFormat) {
        if (!hasDefault(columnFormat)) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                if (s.empty() || isNull(s)) throw noValue("float");
                return (float) DoubleParser.parse(s);
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
                                                    CsvColumnMapping columnFormat) {
        if (!hasDefault(columnFormat)) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                if (s.empty() || isNull(s)) throw noValue("double");
                return DoubleParser.parse(s);
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
            CsvColumnMapping columnFormat) {
        if (!columnFormat.nullable()) {
            return row -> {
                DataSlice s = sliceMapper.apply(row);
                return s.empty() ? null : valueMapper.apply(s);
            };
        }

        T defaultValue = defaultValue(columnFormat);

        return row -> {
            DataSlice s = sliceMapper.apply(row);
            if (s.empty()) return defaultValue;
            return isNull(s) ? defaultValue : valueMapper.apply(s);
        };
    }

    private static boolean hasDefault(CsvColumnMapping columnFormat) {
        return columnFormat.nullable() && columnFormat.defaultValue() != null;
    }

    @SuppressWarnings("unchecked")
    private static <T> T defaultValue(CsvColumnMapping columnFormat) {
        return (T) columnFormat.defaultValue();
    }

    private static IllegalStateException noValue(String type) {
        return new IllegalStateException("missing or null value, and no default is set for the " + type + " column");
    }

    private static boolean isNull(DataSlice slice) {
        return slice == NullableSliceMapper.NULL;
    }
}