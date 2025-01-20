package org.dflib.json;

import org.dflib.BoolValueMapper;
import org.dflib.DoubleValueMapper;
import org.dflib.Extractor;
import org.dflib.FloatValueMapper;
import org.dflib.IntValueMapper;
import org.dflib.LongValueMapper;
import org.dflib.ValueMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

// unlike CSV and other columnar data sources, JSON does not have column positions, so custom
// extractors can be resolved immediately and stored in the map by column name
class ColConfigurator {

    public static Extractor<Map<String, Object>, ?> objectCol(String name, boolean compact) {
        return maybeCompact(Extractor.$col(map -> value(map, name)), compact);
    }

    public static Extractor<Map<String, Object>, ?> objectCol(String name, ValueMapper<Object, ?> mapper, boolean compact) {
        return maybeCompact(Extractor.$col(map -> mapper.map(value(map, name))), compact);
    }

    public static Extractor<Map<String, Object>, ?> intCol(String name) {
        IntValueMapper mapper = IntValueMapper.of();
        return Extractor.$int(map -> mapper.map(value(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> intCol(String name, int forNull) {
        IntValueMapper mapper = IntValueMapper.of(forNull);
        return Extractor.$int(r -> mapper.map(value(r, name)));
    }

    public static Extractor<Map<String, Object>, ?> longCol(String name) {
        LongValueMapper mapper = LongValueMapper.of();
        return Extractor.$long(r -> mapper.map(value(r, name)));
    }

    public static Extractor<Map<String, Object>, ?> longCol(String name, long forNull) {
        LongValueMapper mapper = LongValueMapper.of(forNull);
        return Extractor.$long(r -> mapper.map(value(r, name)));
    }

    /**
     * @since 1.1.0
     */
    public static Extractor<Map<String, Object>, ?> floatCol(String name) {
        FloatValueMapper mapper = FloatValueMapper.of();
        return Extractor.$float(r -> mapper.map(value(r, name)));
    }

    /**
     * @since 1.1.0
     */
    public static Extractor<Map<String, Object>, ?> floatCol(String name, float forNull) {
        FloatValueMapper mapper = FloatValueMapper.of(forNull);
        return Extractor.$float(map -> mapper.map(value(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> doubleCol(String name) {
        DoubleValueMapper mapper = DoubleValueMapper.fromObject();
        return Extractor.$double(r -> mapper.map(value(r, name)));
    }

    public static Extractor<Map<String, Object>, ?> doubleCol(String name, double forNull) {
        DoubleValueMapper mapper = DoubleValueMapper.of(forNull);
        return Extractor.$double(map -> mapper.map(value(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> boolCol(String name) {
        BoolValueMapper<Object> mapper = BoolValueMapper.of();
        return Extractor.$bool(map -> mapper.map(value(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> dateCol(String name) {
        ValueMapper<String, LocalDate> mapper = ValueMapper.stringToDate();
        return Extractor.$col(map -> mapper.map(valueAsString(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> dateCol(String name, DateTimeFormatter formatter) {
        ValueMapper<String, LocalDate> mapper = ValueMapper.stringToDate(formatter);
        return Extractor.$col(map -> mapper.map(valueAsString(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> timeCol(String name) {
        ValueMapper<String, LocalTime> mapper = ValueMapper.stringToTime();
        return Extractor.$col(map -> mapper.map(valueAsString(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> timeCol(String name, DateTimeFormatter formatter) {
        ValueMapper<String, LocalTime> mapper = ValueMapper.stringToTime(formatter);
        return Extractor.$col(map -> mapper.map(valueAsString(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> dateTimeCol(String name) {
        ValueMapper<String, LocalDateTime> mapper = ValueMapper.stringToDateTime();
        return Extractor.$col(map -> mapper.map(valueAsString(map, name)));
    }

    public static Extractor<Map<String, Object>, ?> dateTimeCol(String name, DateTimeFormatter formatter) {
        ValueMapper<String, LocalDateTime> mapper = ValueMapper.stringToDateTime(formatter);
        return Extractor.$col(map -> mapper.map(valueAsString(map, name)));
    }

    private static Extractor<Map<String, Object>, ?> maybeCompact(Extractor<Map<String, Object>, ?> extractor, boolean compact) {
        return compact ? extractor.compact() : extractor;
    }

    static Object value(Map<String, ?> map, String key) {
        return map != null ? map.get(key) : null;
    }

    private static String valueAsString(Map<String, ?> map, String key) {
        if (map == null) {
            return null;
        }

        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }
}
