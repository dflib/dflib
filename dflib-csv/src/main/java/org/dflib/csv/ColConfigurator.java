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
import org.dflib.f.IntBooleanFunction2;

class ColConfigurator {

    int srcColPos;
    String srcColName;
    IntBooleanFunction2<Extractor<CSVRecord, ?>> extractorMaker;
    boolean compact;

    private ColConfigurator() {
        srcColPos = -1;
    }

    public static ColConfigurator objectCol(int pos, boolean compact) {

        IntBooleanFunction2<ValueMapper<CSVRecord, ?>> vm = (i, p) -> p
                ? (r -> i < r.size() ? r.get(i) : null)
                : r -> r.get(i);

        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = (i, p) -> Extractor.$col(vm.apply(i, p));
        config.compact = compact;
        return config;
    }

    public static ColConfigurator objectCol(String name, boolean compact) {
        IntBooleanFunction2<ValueMapper<CSVRecord, ?>> vm = (i, p) -> p
                ? (r -> i < r.size() ? r.get(i) : null)
                : r -> r.get(i);

        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = (i, p) -> Extractor.$col(vm.apply(i, p));
        config.compact = compact;
        return config;
    }

    public static ColConfigurator objectCol(int pos, ValueMapper<String, ?> mapper, boolean compact) {

        IntBooleanFunction2<ValueMapper<CSVRecord, ?>> vm = (i, p) -> p
                ? (r -> i < r.size() ? mapper.map(r.get(i)) : null)
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = (i, p) -> Extractor.$col(vm.apply(i, p));
        config.compact = compact;
        return config;
    }

    public static ColConfigurator objectCol(String name, ValueMapper<String, ?> mapper, boolean compact) {
        IntBooleanFunction2<ValueMapper<CSVRecord, ?>> vm = (i, p) -> p
                ? (r -> i < r.size() ? mapper.map(r.get(i)) : null)
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = (i, p) -> Extractor.$col(vm.apply(i, p));
        config.compact = compact;
        return config;
    }

    public static ColConfigurator intCol(int pos, IntValueMapper<String> mapper) {
        IntBooleanFunction2<IntValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> mapper.map(i < r.size() ? r.get(i) : null))
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = (i, p) -> Extractor.$int(vm.apply(i, p));
        return config;
    }

    public static ColConfigurator intCol(String name, IntValueMapper<String> mapper) {
        IntBooleanFunction2<IntValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> mapper.map(i < r.size() ? r.get(i) : null))
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = (i, p) -> Extractor.$int(vm.apply(i, p));
        return config;
    }

    public static ColConfigurator longCol(int pos, LongValueMapper<String> mapper) {
        IntBooleanFunction2<LongValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> mapper.map(i < r.size() ? r.get(i) : null))
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = (i, p) -> Extractor.$long(vm.apply(i, p));
        return config;
    }

    public static ColConfigurator longCol(String name, LongValueMapper<String> mapper) {
        IntBooleanFunction2<LongValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> mapper.map(i < r.size() ? r.get(i) : null))
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = (i, p) -> Extractor.$long(vm.apply(i, p));
        return config;
    }

    /**
     * @since 1.1.0
     */
    public static ColConfigurator floatCol(int pos, FloatValueMapper<String> mapper) {
        IntBooleanFunction2<FloatValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> mapper.map(i < r.size() ? r.get(i) : null))
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = (i, p) -> Extractor.$float(vm.apply(i, p));
        return config;
    }

    /**
     * @since 1.1.0
     */
    public static ColConfigurator floatCol(String name, FloatValueMapper<String> mapper) {
        IntBooleanFunction2<FloatValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> mapper.map(i < r.size() ? r.get(i) : null))
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = (i, p) -> Extractor.$float(vm.apply(i, p));
        return config;
    }

    public static ColConfigurator doubleCol(int pos, DoubleValueMapper<String> mapper) {

        IntBooleanFunction2<DoubleValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> mapper.map(i < r.size() ? r.get(i) : null))
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = (i, p) -> Extractor.$double(vm.apply(i, p));
        return config;
    }

    public static ColConfigurator doubleCol(String name, DoubleValueMapper<String> mapper) {
        IntBooleanFunction2<DoubleValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> mapper.map(i < r.size() ? r.get(i) : null))
                : r -> mapper.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = (i, p) -> Extractor.$double(vm.apply(i, p));
        return config;
    }

    public static ColConfigurator boolCol(int pos) {
        BoolValueMapper<String> bm = BoolValueMapper.ofStr();
        IntBooleanFunction2<BoolValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> bm.map(i < r.size() ? r.get(i) : null))
                : r -> bm.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColPos = pos;
        config.extractorMaker = (i, p) -> Extractor.$bool(vm.apply(i, p));
        return config;
    }

    public static ColConfigurator boolCol(String name) {
        BoolValueMapper<String> bm = BoolValueMapper.ofStr();
        IntBooleanFunction2<BoolValueMapper<CSVRecord>> vm = (i, p) -> p
                ? (r -> bm.map(i < r.size() ? r.get(i) : null))
                : r -> bm.map(r.get(i));

        ColConfigurator config = new ColConfigurator();
        config.srcColName = name;
        config.extractorMaker = (i, p) -> Extractor.$bool(vm.apply(i, p));
        return config;
    }

    public Extractor<CSVRecord, ?> extractor(Index csvHeader, boolean nullPadRows) {
        int csvPos = srcColPos >= 0 ? srcColPos : csvHeader.position(srcColName);
        Extractor<CSVRecord, ?> e = extractorMaker.apply(csvPos, nullPadRows);
        return compact ? e.compact() : e;
    }
}
