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

    private final Map<String, Extractor<Map<String, Object>, ?>> extractors;
    private final LinkedHashMap<String, SeriesAppender<Map<String, Object>, ?>> appenders;

    JsonLoaderWorker(Map<String, Extractor<Map<String, Object>, ?>> extractors) {
        this.extractors = extractors;
        this.appenders = new LinkedHashMap<>();
    }

    DataFrame load(List<?> parsed) {
        loadColumns(parsed);
        return toDataFrame();
    }

    private void loadColumns(List<?> parsed) {

        // Different maps in the list can have different sets of keys, so columns are added dynamically, and we can't
        // use DataFrameByRowBuilder

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

    private void loadRow(
            Map<String, Object> row,
            int height,
            int offset) {

        for (String column : row.keySet()) {
            appenders.computeIfAbsent(column, n -> appender(n, height, offset)).append(row);
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

    private DataFrame toDataFrame() {
        Index columnsIndex = Index.of(appenders.keySet().toArray(new String[0]));
        int w = appenders.size();
        Series<?>[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = appenders.get(columnsIndex.get(i)).toSeries();
        }

        return new ColumnDataFrame(null, columnsIndex, series);
    }

    private SeriesAppender<Map<String, Object>, ?> appender(String name, int length, int offset) {
        SeriesAppender<Map<String, Object>, ?> appender = new SeriesAppender<>(extractor(name), length);

        // since the appender may have been created in the middle of a loader run, fill the already processed
        // positions with nulls
        for (int i = 0; i < offset; i++) {
            appender.append((Map<String, Object>) null);
        }

        return appender;
    }

    private Extractor<Map<String, Object>, ?> extractor(String columnName) {
        return extractors.computeIfAbsent(columnName, n -> Extractor.$col(m -> ColConfigurator.value(m, n)));
    }
}
