package com.nhl.dflib.csv.loader;

import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.DoubleValueMapper;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntValueMapper;
import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.loader.BooleanExtractor;
import com.nhl.dflib.accumulator.BooleanHolder;
import com.nhl.dflib.loader.DoubleExtractor;
import com.nhl.dflib.accumulator.DoubleHolder;
import com.nhl.dflib.loader.IntExtractor;
import com.nhl.dflib.accumulator.IntHolder;
import com.nhl.dflib.loader.LongExtractor;
import com.nhl.dflib.accumulator.LongHolder;
import com.nhl.dflib.loader.ObjectExtractor;
import com.nhl.dflib.accumulator.ObjectHolder;
import org.apache.commons.csv.CSVRecord;

import java.util.List;

/**
 * Encapsulates user-provided configuration for {@link com.nhl.dflib.csv.CsvLoader}.
 *
 * @since 0.8
 */
public class ColumnConfig {

    private int columnPosition;
    private String columnName;
    private ColumnType type;

    private ValueMapper<String, ?> objectMapper;
    private IntValueMapper<String> intMapper;
    private LongValueMapper<String> longMapper;
    private DoubleValueMapper<String> doubleMapper;
    private BooleanValueMapper<String> booleanMapper;

    private ColumnConfig() {
        columnPosition = -1;
    }

    public static ColumnConfig[] columnBuilders(Index columns, List<ColumnConfig> configs) {
        int w = columns.size();
        ColumnConfig[] normalized = new ColumnConfig[w];

        for (ColumnConfig config : configs) {
            // later configs override earlier configs at the same position
            int pos = config.columnPosition >= 0 ? config.columnPosition : columns.position(config.columnName);
            normalized[pos] = config;
        }

        // fill empty positions
        for (int i = 0; i < w; i++) {
            if (normalized[i] == null) {
                normalized[i] = objectColumn(i, v -> v);
            }
        }

        return normalized;
    }

    public static ColumnConfig objectColumn(int pos, ValueMapper<String, ?> mapper) {

        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.object;
        config.columnPosition = pos;
        config.objectMapper = mapper;
        return config;
    }

    public static ColumnConfig objectColumn(String name, ValueMapper<String, ?> mapper) {

        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.object;
        config.columnName = name;
        config.objectMapper = mapper;
        return config;
    }

    public static ColumnConfig intColumn(int pos) {
        return intColumn(pos, IntValueMapper.fromString());
    }

    public static ColumnConfig intColumn(String name) {
        return intColumn(name, IntValueMapper.fromString());
    }

    public static ColumnConfig intColumn(int pos, IntValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.intPrimitive;
        config.columnPosition = pos;
        config.intMapper = mapper;
        return config;
    }

    public static ColumnConfig intColumn(String name, IntValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.intPrimitive;
        config.columnName = name;
        config.intMapper = mapper;
        return config;
    }

    public static ColumnConfig longColumn(int pos) {
        return longColumn(pos, LongValueMapper.fromString());
    }

    public static ColumnConfig longColumn(String name) {
        return longColumn(name, LongValueMapper.fromString());
    }

    public static ColumnConfig longColumn(int pos, LongValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.longPrimitive;
        config.columnPosition = pos;
        config.longMapper = mapper;
        return config;
    }

    public static ColumnConfig longColumn(String name, LongValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.longPrimitive;
        config.columnName = name;
        config.longMapper = mapper;
        return config;
    }

    public static ColumnConfig doubleColumn(int pos) {
        return doubleColumn(pos, DoubleValueMapper.fromString());
    }

    public static ColumnConfig doubleColumn(String name) {
        return doubleColumn(name, DoubleValueMapper.fromString());
    }

    public static ColumnConfig doubleColumn(int pos, DoubleValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.doublePrimitive;
        config.columnPosition = pos;
        config.doubleMapper = mapper;
        return config;
    }

    public static ColumnConfig doubleColumn(String name, DoubleValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.doublePrimitive;
        config.columnName = name;
        config.doubleMapper = mapper;
        return config;
    }

    public static ColumnConfig booleanColumn(int pos) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.booleanPrimitive;
        config.columnPosition = pos;
        config.booleanMapper = BooleanValueMapper.fromString();
        return config;
    }

    public static ColumnConfig booleanColumn(String name) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.booleanPrimitive;
        config.columnName = name;
        config.booleanMapper = BooleanValueMapper.fromString();
        return config;
    }

    public CsvSeriesBuilder<?> createColumnBuilder(int csvPos) {

        // using externally passed "csvPos", as "this.columnPosition" may not be initialized (when column name
        // was in use)

        switch (type) {
            case intPrimitive:
                return new CsvSeriesBuilder<>(intConverter(csvPos), csvPos);
            case longPrimitive:
                return new CsvSeriesBuilder<>(longConverter(csvPos), csvPos);
            case doublePrimitive:
                return new CsvSeriesBuilder<>(doubleConverter(csvPos), csvPos);
            case booleanPrimitive:
                return new CsvSeriesBuilder<>(boolConverter(csvPos), csvPos);
            default:
                return new CsvSeriesBuilder(objectConverter(csvPos), csvPos);
        }
    }

    public CsvCell<?> createValueHolderColumn(int csvPos) {

        switch (type) {
            case intPrimitive:
                return new CsvCell<>(intConverter(csvPos), new IntHolder());
            case longPrimitive:
                return new CsvCell<>(longConverter(csvPos), new LongHolder());
            case doublePrimitive:
                return new CsvCell<>(doubleConverter(csvPos), new DoubleHolder());
            case booleanPrimitive:
                return new CsvCell<>(boolConverter(csvPos), new BooleanHolder());
            default:
                return new CsvCell<>(objectConverter(csvPos), new ObjectHolder());
        }
    }

    private IntExtractor<CSVRecord> intConverter(int csvPos) {
        return new IntExtractor<>(r -> intMapper.map(r.get(csvPos)));
    }

    private LongExtractor<CSVRecord> longConverter(int csvPos) {
        return new LongExtractor<>(r -> longMapper.map(r.get(csvPos)));
    }

    private DoubleExtractor<CSVRecord> doubleConverter(int csvPos) {
        return new DoubleExtractor<>(r -> doubleMapper.map(r.get(csvPos)));
    }

    private BooleanExtractor<CSVRecord> boolConverter(int csvPos) {
        return new BooleanExtractor<>(r -> booleanMapper.map(r.get(csvPos)));
    }

    private ObjectExtractor<CSVRecord, ?> objectConverter(int csvPos) {
        return new ObjectExtractor<>(r -> objectMapper.map(r.get(csvPos)));
    }

    enum ColumnType {
        intPrimitive, longPrimitive, doublePrimitive, booleanPrimitive, object
    }
}
