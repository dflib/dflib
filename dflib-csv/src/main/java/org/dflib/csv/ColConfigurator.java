package org.dflib.csv;

import org.apache.commons.csv.CSVRecord;
import org.dflib.BoolValueMapper;
import org.dflib.DoubleValueMapper;
import org.dflib.Extractor;
import org.dflib.FloatValueMapper;
import org.dflib.Index;
import org.dflib.IntValueMapper;
import org.dflib.LongValueMapper;
import org.dflib.ValueMapper;

import java.util.function.IntFunction;

class ColConfigurator {

    int srcColPos;
    String srcColName;
    IntFunction<Extractor<CSVRecord, ?>> extractorMaker;
    boolean compact;

    private ColConfigurator() {
        srcColPos = -1;
    }

    public static ColConfigurator objectCol(int pos, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = i -> Extractor.$col(r -> r.get(i));
        config.compact = compact;
        return config;
    }

    public static ColConfigurator objectCol(String name, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = i -> Extractor.$col(r -> r.get(i));
        config.compact = compact;
        return config;
    }

    public static ColConfigurator objectCol(int pos, ValueMapper<String, ?> mapper, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = i -> Extractor.$col(r -> mapper.map(r.get(i)));
        config.compact = compact;
        return config;
    }

    public static ColConfigurator objectCol(String name, ValueMapper<String, ?> mapper, boolean compact) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = i -> Extractor.$col(r -> mapper.map(r.get(i)));
        config.compact = compact;
        return config;
    }

    public static ColConfigurator intCol(int pos, IntValueMapper<String> mapper) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = i -> Extractor.$int(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColConfigurator intCol(String name, IntValueMapper<String> mapper) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = i -> Extractor.$int(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColConfigurator longCol(int pos, LongValueMapper<String> mapper) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = i -> Extractor.$long(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColConfigurator longCol(String name, LongValueMapper<String> mapper) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = i -> Extractor.$long(r -> mapper.map(r.get(i)));
        return config;
    }

    /**
     * @since 1.1.0
     */
    public static ColConfigurator floatCol(int pos, FloatValueMapper<String> mapper) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = i -> Extractor.$float(r -> mapper.map(r.get(i)));
        return config;
    }

    /**
     * @since 1.1.0
     */
    public static ColConfigurator floatCol(String name, FloatValueMapper<String> mapper) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = i -> Extractor.$float(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColConfigurator doubleCol(int pos, DoubleValueMapper<String> mapper) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = i -> Extractor.$double(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColConfigurator doubleCol(String name, DoubleValueMapper<String> mapper) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = i -> Extractor.$double(r -> mapper.map(r.get(i)));
        return config;
    }

    public static ColConfigurator boolCol(int pos) {
        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = i -> Extractor.$bool(r -> BoolValueMapper.fromString().map(r.get(i)));
        return config;
    }

    public static ColConfigurator boolCol(String name) {
        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = i -> Extractor.$bool(r -> BoolValueMapper.fromString().map(r.get(i)));
        return config;
    }

    public Extractor<CSVRecord, ?> extractor(Index csvHeader) {
        int csvPos = srcColPos >= 0 ? srcColPos : csvHeader.position(srcColName);
        Extractor<CSVRecord, ?> e = extractorMaker.apply(csvPos);
        return compact ? e.compact() : e;
    }
}
