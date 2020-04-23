package com.nhl.dflib.json;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.io.Reader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @since 0.8
 */
public class JsonLoader {

    private static String DEFAULT_SCALAR_COLUMN = "_val";

    private Set<Option> options;
    private String pathExpression;

    public JsonLoader() {
        this.pathExpression = "$.*";
        this.options = new HashSet<>();
        options.add(Option.ALWAYS_RETURN_LIST);
    }

    /**
     * Pass a JSONPath expression to navigate to the root collection. Default path is "$.*", that assumes the top element is
     * either a list (whose elements are loaded as rows), or an object (whose properties are loaded as rows).
     *
     * @param pathExpression a JSONPath expression
     * @return this loader
     * @see <a href="http://jsonpath.herokuapp.com/?path=$..*">JSONPath online evaludator</a>
     */
    public JsonLoader pathExpression(String pathExpression) {
        this.pathExpression = Objects.requireNonNull(pathExpression);
        return this;
    }

    public JsonLoader nullsForMissingLeafs() {
        this.options.add(Option.DEFAULT_PATH_LEAF_TO_NULL);
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
        LinkedHashMap<String, Accumulator<Object>> columns = loadColumns(parsed);
        return toDataFrame(columns);
    }

    protected LinkedHashMap<String, Accumulator<Object>> loadColumns(List<Map<String, Object>> parsed) {

        // Different maps in the list can have different sets of keys...
        // Algorithm below aligns values by row..

        int height = parsed.size();
        int offset = 0;
        LinkedHashMap<String, Accumulator<Object>> columns = new LinkedHashMap<>();
        for (Object row : parsed) {
            int localOffset = offset;

            if (row instanceof Map) {
                loadRowAsMap((Map<String, Object>) row, columns, height, offset);
            } else {
                loadRowAsScalar(row, columns, height, offset);
            }

            offset++;
        }

        return columns;
    }

    protected void loadRowAsScalar(Object row, LinkedHashMap<String, Accumulator<Object>> columns, int height, int offset) {
        columns.computeIfAbsent(DEFAULT_SCALAR_COLUMN, label -> createdColumnAccumulator(height, offset)).add(row);
    }

    protected void loadRowAsMap(Map<String, Object> row, LinkedHashMap<String, Accumulator<Object>> columns, int height, int offset) {

        for (Map.Entry<String, Object> e : row.entrySet()) {
            columns.computeIfAbsent(e.getKey(), label -> createdColumnAccumulator(height, offset)).add(e.getValue());
        }

        // columns not in this record must be filled with nulls
        if (columns.size() > row.size()) {
            for (Map.Entry<String, Accumulator<Object>> e : columns.entrySet()) {
                if (!row.containsKey(e.getKey())) {
                    e.getValue().add(null);
                }
            }
        }
    }

    protected DataFrame toDataFrame(LinkedHashMap<String, Accumulator<Object>> columnData) {
        Index columnsIndex = Index.forLabels(columnData.keySet().toArray(new String[0]));
        int w = columnData.size();
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = columnData.get(columnsIndex.getLabel(i)).toSeries();
        }

        return new ColumnDataFrame(columnsIndex, series);
    }

    protected Accumulator<Object> createdColumnAccumulator(int length, int offset) {
        Accumulator<Object> column = new ObjectAccumulator<>(length);

        for (int i = 0; i < offset; i++) {
            column.add(null);
        }

        return column;
    }
}
