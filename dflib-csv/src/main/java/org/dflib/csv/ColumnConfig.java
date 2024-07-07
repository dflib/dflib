package org.dflib.csv;

import org.dflib.BoolValueMapper;
import org.dflib.DoubleValueMapper;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.IntValueMapper;
import org.dflib.LongValueMapper;
import org.dflib.ValueMapper;
import org.apache.commons.csv.CSVRecord;

import java.util.function.IntFunction;

class ColumnConfig {

    int csvColPos;
    String csvColName;
    IntFunction<Extractor<CSVRecord, ?>> extractorMaker;

    private ColumnConfig() {
        csvColPos = -1;
    }

    public static ColumnConfig objectCol(int pos, ValueMapper<String, ?> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.csvColPos = pos;
        config.extractorMaker = i -> Extractor.$col(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColumnConfig objectCol(String name, ValueMapper<String, ?> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.csvColName = name;
        config.extractorMaker = i -> Extractor.$col(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColumnConfig intCol(int pos, IntValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.csvColPos = pos;
        config.extractorMaker = i -> Extractor.$int(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColumnConfig intCol(String name, IntValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.csvColName = name;
        config.extractorMaker = i -> Extractor.$int(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColumnConfig longCol(int pos, LongValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.csvColPos = pos;
        config.extractorMaker = i -> Extractor.$long(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColumnConfig longCol(String name, LongValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.csvColName = name;
        config.extractorMaker = i -> Extractor.$long(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColumnConfig doubleCol(int pos, DoubleValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.csvColPos = pos;
        config.extractorMaker = i -> Extractor.$double(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColumnConfig doubleCol(String name, DoubleValueMapper<String> mapper) {
        ColumnConfig config = new ColumnConfig();
        config.csvColName = name;
        config.extractorMaker = i -> Extractor.$double(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColumnConfig boolCol(int pos) {
        ColumnConfig config = new ColumnConfig();
        config.csvColPos = pos;
        config.extractorMaker = i -> Extractor.$bool(r -> BoolValueMapper.fromString().map(r.get(i)));
        return config;
    }

    public static ColumnConfig boolCol(String name) {
        ColumnConfig config = new ColumnConfig();
        config.csvColName = name;
        config.extractorMaker = i -> Extractor.$bool(r -> BoolValueMapper.fromString().map(r.get(i)));
        return config;
    }

    public Extractor<CSVRecord, ?> extractor(Index csvHeader) {
        int csvPos = csvColPos >= 0 ? csvColPos : csvHeader.position(csvColName);
        return extractorMaker.apply(csvPos);
    }
}
