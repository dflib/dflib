package com.nhl.dflib.csv.loader;

import com.nhl.dflib.accumulator.ValueAccum;
import com.nhl.dflib.loader.ValueExtractor;
import com.nhl.dflib.accumulator.ValueHolder;
import org.apache.commons.csv.CSVRecord;

/**
 * An intermediary holder of CSV values that allows to do conversion and filtering before accumulating values for
 * the DataFrame. This is a flyweight reused for multiple rows.
 *
 * @since 0.8
 */
public class CsvCell<T> {

    private final ValueExtractor<CSVRecord, T> converter;
    private final ValueHolder<T> holder;

    public CsvCell(ValueExtractor<CSVRecord, T> converter, ValueHolder<T> holder) {
        this.converter = converter;
        this.holder = holder;
    }

    public void set(CSVRecord record) {
        converter.extract(record, holder);
    }

    public void store(ValueAccum<T> accumulator) {
        holder.store(accumulator);
    }

    public void store(int pos, ValueAccum<T> accumulator) {
        holder.store(accumulator, pos);
    }

    public T get() {
        return holder.get();
    }
}
