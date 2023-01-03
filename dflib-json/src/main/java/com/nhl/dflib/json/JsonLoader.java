package com.nhl.dflib.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DoubleValueMapper;
import com.nhl.dflib.Extractor;
import com.nhl.dflib.IntValueMapper;
import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.ValueMapper;

import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @since 0.8
 */
public class JsonLoader {

    private final Set<Option> options;
    private final Map<String, Extractor<Map<String, Object>, ?>> extractorPresets;

    private String pathExpression;

    public JsonLoader() {
        this.pathExpression = "$.*";
        this.options = new HashSet<>();
        options.add(Option.ALWAYS_RETURN_LIST);

        this.extractorPresets = new HashMap<>();
    }

    /**
     * Pass a JSONPath expression to navigate to the root collection. Default path is "$.*", that assumes the top element is
     * either a list (whose elements are loaded as rows), or an object (whose properties are loaded as rows).
     *
     * @param pathExpression a JSONPath expression
     * @return this loader
     * @see <a href="http://jsonpath.herokuapp.com/?path=$..*">JSONPath online evaluator</a>
     */
    public JsonLoader pathExpression(String pathExpression) {
        this.pathExpression = Objects.requireNonNull(pathExpression);
        return this;
    }

    public JsonLoader nullsForMissingLeafs() {
        this.options.add(Option.DEFAULT_PATH_LEAF_TO_NULL);
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader columnType(String column, ValueMapper<Object, ?> mapper) {
        extractorPresets.put(column, customExtractor(column, mapper));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader boolColumn(String column) {
        extractorPresets.put(column, boolExtractor(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader intColumn(String column) {
        extractorPresets.put(column, intExtractor(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader intColumn(String column, int forNull) {
        extractorPresets.put(column, intExtractor(column, forNull));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader longColumn(String column) {
        extractorPresets.put(column, longExtractor(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader longColumn(String column, long forNull) {
        extractorPresets.put(column, longExtractor(column, forNull));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader doubleColumn(String column) {
        extractorPresets.put(column, doubleExtractor(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader doubleColumn(String column, double forNull) {
        extractorPresets.put(column, doubleExtractor(column, forNull));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader dateColumn(String column) {
        extractorPresets.put(column, dateExtractor(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader dateColumn(String column, DateTimeFormatter formatter) {
        extractorPresets.put(column, dateExtractor(column, formatter));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader timeColumn(String column) {
        extractorPresets.put(column, timeExtractor(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader timeColumn(String column, DateTimeFormatter formatter) {
        extractorPresets.put(column, timeExtractor(column, formatter));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader dateTimeColumn(String column) {
        extractorPresets.put(column, dateTimeExtractor(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader dateTimeColumn(String column, DateTimeFormatter formatter) {
        extractorPresets.put(column, dateTimeExtractor(column, formatter));
        return this;
    }


    public DataFrame load(String json) {
        DocumentContext context = JsonPath.parse(json, buildJSONPathConfiguration());
        return load(context);
    }

    public DataFrame load(Reader reader) {
        DocumentContext context = JsonPath.parse(reader, buildJSONPathConfiguration());
        return load(context);
    }

    protected Configuration buildJSONPathConfiguration() {
        return Configuration.builder().options(options).build();
    }

    protected DataFrame load(DocumentContext context) {
        List<Map<String, Object>> parsed = context.read(pathExpression);
        return new JsonLoaderWorker(extractorPresets).load(parsed);
    }

    static Extractor<Map<String, Object>, Object> defaultExtractor(String name) {
        return Extractor.$col(m -> getObject(m, name));
    }

    static Extractor<Map<String, Object>, Object> customExtractor(String name, ValueMapper<Object, ?> mapper) {
        return Extractor.$col(m -> mapper.map(getObject(m, name)));
    }

    static Extractor<Map<String, Object>, Boolean> boolExtractor(String name) {
        return Extractor.$bool(m -> BooleanValueMapper.fromObject().map(getObject(m, name)));
    }

    static Extractor<Map<String, Object>, Integer> intExtractor(String name) {
        return Extractor.$int(m -> IntValueMapper.fromObject().map(getObject(m, name)));
    }

    static Extractor<Map<String, Object>, Integer> intExtractor(String name, int fillNulls) {
        IntValueMapper<Object> mapper = IntValueMapper.fromObject(fillNulls);
        return Extractor.$int(m -> mapper.map(getObject(m, name)));
    }

    static Extractor<Map<String, Object>, Long> longExtractor(String name) {
        return Extractor.$long(m -> LongValueMapper.fromObject().map(getObject(m, name)));
    }

    static Extractor<Map<String, Object>, Long> longExtractor(String name, long fillNulls) {
        LongValueMapper<Object> mapper = LongValueMapper.fromObject(fillNulls);
        return Extractor.$long(m -> mapper.map(getObject(m, name)));
    }

    static Extractor<Map<String, Object>, Double> doubleExtractor(String name) {
        return Extractor.$double(m -> DoubleValueMapper.fromObject().map(getObject(m, name)));
    }

    static Extractor<Map<String, Object>, Double> doubleExtractor(String name, double fillNulls) {
        DoubleValueMapper<Object> mapper = DoubleValueMapper.fromObject(fillNulls);
        return Extractor.$double(m -> mapper.map(getObject(m, name)));
    }

    static Extractor<Map<String, Object>, LocalDate> dateExtractor(String name) {
        return Extractor.$col(m -> ValueMapper.stringToDate().map(getString(m, name)));
    }

    static Extractor<Map<String, Object>, LocalDate> dateExtractor(String name, DateTimeFormatter formatter) {
        ValueMapper<String, LocalDate> mapper = ValueMapper.stringToDate(formatter);
        return Extractor.$col(m -> mapper.map(getString(m, name)));
    }

    static Extractor<Map<String, Object>, LocalTime> timeExtractor(String name) {
        return Extractor.$col(m -> ValueMapper.stringToTime().map(getString(m, name)));
    }

    static Extractor<Map<String, Object>, LocalTime> timeExtractor(String name, DateTimeFormatter formatter) {
        ValueMapper<String, LocalTime> mapper = ValueMapper.stringToTime(formatter);
        return Extractor.$col(m -> mapper.map(getString(m, name)));
    }

    static Extractor<Map<String, Object>, LocalDateTime> dateTimeExtractor(String name) {
        return Extractor.$col(m -> ValueMapper.stringToDateTime().map(getString(m, name)));
    }

    static Extractor<Map<String, Object>, LocalDateTime> dateTimeExtractor(String name, DateTimeFormatter formatter) {
        ValueMapper<String, LocalDateTime> mapper = ValueMapper.stringToDateTime(formatter);
        return Extractor.$col(m -> mapper.map(getString(m, name)));
    }

    private static Object getObject(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }

        return map.get(key);
    }

    private static String getString(Map<String, Object> map, String key) {
        if (map == null) {
            return null;
        }

        Object val = map.get(key);
        return val != null ? val.toString() : null;
    }
}
