package org.dflib.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.ValueMapper;
import org.dflib.ByteSource;
import org.dflib.ByteSources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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


    public JsonLoader col(String column, ValueMapper<Object, ?> mapper) {
        extractors.put(column, ColConfigurator.objectCol(column, mapper, false));
        return this;
    }

    /**
     * Configures a column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     */
    public JsonLoader compactCol(String column) {
        extractors.put(column, ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures a column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     */
    public JsonLoader compactCol(String column, ValueMapper<Object, ?> mapper) {
        extractors.put(column, ColConfigurator.objectCol(column, mapper, true));
        return this;
    }


    public JsonLoader boolCol(String column) {
        extractors.put(column, ColConfigurator.boolCol(column));
        return this;
    }


    public JsonLoader intCol(String column) {
        extractors.put(column, ColConfigurator.intCol(column));
        return this;
    }


    public JsonLoader intCol(String column, int forNull) {
        extractors.put(column, ColConfigurator.intCol(column, forNull));
        return this;
    }


    public JsonLoader longColumn(String column) {
        extractors.put(column, ColConfigurator.longCol(column));
        return this;
    }


    public JsonLoader longColumn(String column, long forNull) {
        extractors.put(column, ColConfigurator.longCol(column, forNull));
        return this;
    }


    public JsonLoader doubleCol(String column) {
        extractors.put(column, ColConfigurator.doubleCol(column));
        return this;
    }


    public JsonLoader doubleCol(String column, double forNull) {
        extractors.put(column, ColConfigurator.doubleCol(column, forNull));
        return this;
    }


    public JsonLoader dateCol(String column) {
        extractors.put(column, ColConfigurator.dateCol(column));
        return this;
    }


    public JsonLoader dateCol(String column, DateTimeFormatter formatter) {
        extractors.put(column, ColConfigurator.dateCol(column, formatter));
        return this;
    }


    public JsonLoader timeCol(String column) {
        extractors.put(column, ColConfigurator.timeCol(column));
        return this;
    }


    public JsonLoader timeCol(String column, DateTimeFormatter formatter) {
        extractors.put(column, ColConfigurator.timeCol(column, formatter));
        return this;
    }


    public JsonLoader dateTimeCol(String column) {
        extractors.put(column, ColConfigurator.dateTimeCol(column));
        return this;
    }


    public JsonLoader dateTimeCol(String column, DateTimeFormatter formatter) {
        extractors.put(column, ColConfigurator.dateTimeCol(column, formatter));
        return this;
    }

    /**
     * Loads a DataFrame from a JSON file at the specified path.
     */
    public DataFrame load(Path filePath) {
        return load(ByteSource.ofPath(filePath));
    }

    /**
     * Loads a DataFrame from a JSON file.
     */
    public DataFrame load(File file) {
        return load(ByteSource.ofFile(file));
    }

    /**
     * Loads a DataFrame from the provided InputStream
     */
    public DataFrame load(InputStream in) {
        DocumentContext context = JsonPath.parse(in, buildJSONPathConfiguration());
        return loadFromContext(context);
    }

    /**
     * @since 1.1.0
     */
    public DataFrame load(ByteSource src) {
        return src.processStream(this::load);
    }

    /**
     * @since 1.1.0
     */
    public Map<String, DataFrame> loadAll(ByteSources src) {
        return src.processStreams((name, st) -> load(st));
    }

    public DataFrame load(Reader reader) {
        return load(loadJsonFromReader(reader));
    }

    /**
     * Loads a DataFrame from the provided JSON String. Note that unlike a few other loaders, the String here denotes
     * JSON content, not the file name.
     */
    public DataFrame load(String json) {
        DocumentContext context = JsonPath.parse(json, buildJSONPathConfiguration());
        return loadFromContext(context);
    }

    protected DataFrame loadFromContext(DocumentContext context) {
        List<?> parsed = context.read(pathExpression);
        return new JsonLoaderWorker(extractors).load(parsed);
    }

    private String loadJsonFromReader(Reader reader) {
        StringBuilder buf = new StringBuilder();

        int len = 8192;
        char[] chars = new char[len];
        int read;

        try {
            while ((read = reader.read(chars, 0, len)) != -1) {
                buf.append(chars, 0, read);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading JSON", e);
        }

        return buf.toString();
    }

    protected Configuration buildJSONPathConfiguration() {
        return Configuration.builder().options(options).build();
    }
}
