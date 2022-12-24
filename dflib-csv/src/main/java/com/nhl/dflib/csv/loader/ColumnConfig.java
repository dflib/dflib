package com.nhl.dflib.csv.loader;

import com.nhl.dflib.BooleanValueMapper;
import com.nhl.dflib.DoubleValueMapper;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntValueMapper;
import com.nhl.dflib.LongValueMapper;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.accumulator.BooleanAccumulator;
import com.nhl.dflib.accumulator.BooleanConverter;
import com.nhl.dflib.accumulator.BooleanHolder;
import com.nhl.dflib.accumulator.DoubleAccumulator;
import com.nhl.dflib.accumulator.DoubleConverter;
import com.nhl.dflib.accumulator.DoubleHolder;
import com.nhl.dflib.accumulator.IntAccumulator;
import com.nhl.dflib.accumulator.IntConverter;
import com.nhl.dflib.accumulator.IntHolder;
import com.nhl.dflib.accumulator.LongAccumulator;
import com.nhl.dflib.accumulator.LongConverter;
import com.nhl.dflib.accumulator.LongHolder;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.accumulator.ObjectConverter;
import com.nhl.dflib.accumulator.ObjectHolder;

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

    private ObjectConverter<String, ?> objectConverter;
    private IntConverter<String> intConverter;
    private LongConverter<String> longConverter;
    private DoubleConverter<String> doubleConverter;
    private BooleanConverter<String> booleanConverter;

    private ColumnConfig() {
        columnPosition = -1;
    }

    public static ColumnConfig[] normalize(Index columns, List<ColumnConfig> configs) {
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
        config.objectConverter = new ObjectConverter<>(mapper);
        return config;
    }

    public static ColumnConfig objectColumn(String name, ValueMapper<String, ?> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.object;
        config.columnName = name;
        config.objectConverter = new ObjectConverter<>(mapper);
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
        config.intConverter = new IntConverter<>(mapper);
        return config;
    }

    public static ColumnConfig intColumn(String name, IntValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.intPrimitive;
        config.columnName = name;
        config.intConverter = new IntConverter<>(mapper);
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
        config.longConverter = new LongConverter<>(mapper);
        return config;
    }

    public static ColumnConfig longColumn(String name, LongValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.longPrimitive;
        config.columnName = name;
        config.longConverter = new LongConverter<>(mapper);
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
        config.doubleConverter = new DoubleConverter<>(mapper);
        return config;
    }

    public static ColumnConfig doubleColumn(String name, DoubleValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.doublePrimitive;
        config.columnName = name;
        config.doubleConverter = new DoubleConverter<>(mapper);
        return config;
    }

    public static ColumnConfig booleanColumn(int pos) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.booleanPrimitive;
        config.columnPosition = pos;
        config.booleanConverter = new BooleanConverter<>(BooleanValueMapper.fromString());
        return config;
    }

    public static ColumnConfig booleanColumn(String name) {
        ColumnConfig config = new ColumnConfig();
        config.type = ColumnType.booleanPrimitive;
        config.columnName = name;
        config.booleanConverter = new BooleanConverter<>(BooleanValueMapper.fromString());
        return config;
    }

    public ColumnBuilder<?> createColumnBuilder(int columnPosition) {

        // using externally passed "columnPosition", as "this.columnPosition" may not be initialized (when column name
        // was in use)

        switch (type) {
            case intPrimitive:
                return new ColumnBuilder<>(intConverter, new IntAccumulator(), columnPosition);
            case longPrimitive:
                return new ColumnBuilder<>(longConverter, new LongAccumulator(), columnPosition);
            case doublePrimitive:
                return new ColumnBuilder<>(doubleConverter, new DoubleAccumulator(), columnPosition);
            case booleanPrimitive:
                return new ColumnBuilder<>(booleanConverter, new BooleanAccumulator(), columnPosition);
            default:
                return new ColumnBuilder(objectConverter, new ObjectAccumulator<>(), columnPosition);
        }
    }

    public CsvCell<?> createValueHolderColumn(int columnPosition) {

        // using externally passed "columnPosition", as "this.columnPosition" may not be initialized (when column name
        // was in use)

        switch (type) {
            case intPrimitive:
                return new CsvCell<>(intConverter, new IntHolder(), columnPosition);
            case longPrimitive:
                return new CsvCell<>(longConverter, new LongHolder(), columnPosition);
            case doublePrimitive:
                return new CsvCell<>(doubleConverter, new DoubleHolder(), columnPosition);
            case booleanPrimitive:
                return new CsvCell<>(booleanConverter, new BooleanHolder(), columnPosition);
            default:
                return new CsvCell(objectConverter, new ObjectHolder(), columnPosition);
        }
    }

    enum ColumnType {
        intPrimitive, longPrimitive, doublePrimitive, booleanPrimitive, object
    }
}
