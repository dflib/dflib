package org.dflib.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.ValueMapper;

import java.io.Reader;
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

    // unlike CSV and other columnar data sources, JSON does not have column positions, so custom
    // extractors can be resolved immediately and stored in the map by column name
    private final Map<String, Extractor<Map<String, Object>, ?>> extractors;

    private String pathExpression;

    public JsonLoader() {
        this.pathExpression = "$.*";
        this.options = new HashSet<>();
        options.add(Option.ALWAYS_RETURN_LIST);

        this.extractors = new HashMap<>();
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

    /**
     * If set, prevents skipping rows that are missing properties defined in the path expression. Instead
     * of skipping those rows, the loader would fill those positions with null.
     */
    public JsonLoader nullsForMissingLeafs() {
        this.options.add(Option.DEFAULT_PATH_LEAF_TO_NULL);
        return this;
    }

    /**
     * @since 1.0.0-RC1
     */
    public JsonLoader col(String column, ValueMapper<Object, ?> mapper) {
        extractors.put(column, ColConfigurator.objectCol(column, mapper, false));
        return this;
    }

    /**
     * Configures a column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     *
     * @since 1.0.0-RC1
     */
    public JsonLoader compactCol(String column) {
        extractors.put(column, ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures a column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     *
     * @since 1.0.0-RC1
     */
    public JsonLoader compactCol(String column, ValueMapper<Object, ?> mapper) {
        extractors.put(column, ColConfigurator.objectCol(column, mapper, true));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader boolCol(String column) {
        extractors.put(column, ColConfigurator.boolCol(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader intCol(String column) {
        extractors.put(column, ColConfigurator.intCol(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader intCol(String column, int forNull) {
        extractors.put(column, ColConfigurator.intCol(column, forNull));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader longColumn(String column) {
        extractors.put(column, ColConfigurator.longCol(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader longColumn(String column, long forNull) {
        extractors.put(column, ColConfigurator.longCol(column, forNull));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader doubleCol(String column) {
        extractors.put(column, ColConfigurator.doubleCol(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader doubleCol(String column, double forNull) {
        extractors.put(column, ColConfigurator.doubleCol(column, forNull));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader dateCol(String column) {
        extractors.put(column, ColConfigurator.dateCol(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader dateCol(String column, DateTimeFormatter formatter) {
        extractors.put(column, ColConfigurator.dateCol(column, formatter));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader timeCol(String column) {
        extractors.put(column, ColConfigurator.timeCol(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader timeCol(String column, DateTimeFormatter formatter) {
        extractors.put(column, ColConfigurator.timeCol(column, formatter));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader dateTimeCol(String column) {
        extractors.put(column, ColConfigurator.dateTimeCol(column));
        return this;
    }

    /**
     * @since 0.16
     */
    public JsonLoader dateTimeCol(String column, DateTimeFormatter formatter) {
        extractors.put(column, ColConfigurator.dateTimeCol(column, formatter));
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

    protected DataFrame load(DocumentContext context) {
        List<?> parsed = context.read(pathExpression);
        return new JsonLoaderWorker(extractors).load(parsed);
    }

    protected Configuration buildJSONPathConfiguration() {
        return Configuration.builder().options(options).build();
    }
}
