package org.dflib.json;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.builder.SeriesAppender;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class JsonLoaderWorker {

    private static final String DEFAULT_SCALAR_COLUMN = "_val";

    private final Map<String, Extractor<Map<String, Object>, ?>> extractorPresets;
    private final LinkedHashMap<String, SeriesAppender<Map<String, Object>, ?>> appenders;

    JsonLoaderWorker(Map<String, Extractor<Map<String, Object>, ?>> extractorPresets) {
        this.extractorPresets = extractorPresets;
        this.appenders = new LinkedHashMap<>();
    }

    protected DataFrame load(List<Map<String, Object>> parsed) {
        loadColumns(parsed);
        return toDataFrame();
    }

    protected void loadColumns(List<Map<String, Object>> parsed) {

        // Different maps in the list can have different sets of keys...
        // Algorithm below aligns values by row..

        int height = parsed.size();
        int offset = 0;

        for (Object row : parsed) {

            if (!(row instanceof Map)) {
                // using Collections.singletonMap instead of Map.of , as the former supports nulls
                row = Collections.singletonMap(DEFAULT_SCALAR_COLUMN, row);
            }

            loadRow((Map<String, Object>) row, height, offset);

            offset++;
        }
    }

    protected void loadRow(
            Map<String, Object> row,
            int height,
            int offset) {

        for (String column : row.keySet()) {
            appenders.computeIfAbsent(column, label -> createdAppender(column, height, offset)).append(row);
        }

        // columns not in this record must be filled with nulls
        if (appenders.size() > row.size()) {
            for (Map.Entry<String, SeriesAppender<Map<String, Object>, ?>> e : appenders.entrySet()) {
                if (!row.containsKey(e.getKey())) {
                    e.getValue().append((Map<String, Object>) null);
                }
            }
        }
    }

    protected DataFrame toDataFrame() {
        Index columnsIndex = Index.of(appenders.keySet().toArray(new String[0]));
        int w = appenders.size();
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = appenders.get(columnsIndex.getLabel(i)).toSeries();
        }

        return new ColumnDataFrame(null, columnsIndex, series);
    }

    protected SeriesAppender<Map<String, Object>, ?> createdAppender(String name, int length, int offset) {
        Extractor<Map<String, Object>, ?> extractorPreset = extractorPresets.get(name);
        Extractor<Map<String, Object>, ?> extractor = extractorPreset != null ? extractorPreset : JsonLoader.defaultExtractor(name);

        SeriesAppender<Map<String, Object>, ?> appender = new SeriesAppender<>(extractor, length);
        for (int i = 0; i < offset; i++) {
            appender.append((Map<String, Object>) null);
        }

        return appender;
    }
}
