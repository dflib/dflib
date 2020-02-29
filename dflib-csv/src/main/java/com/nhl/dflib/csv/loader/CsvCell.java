package com.nhl.dflib.csv.loader;

import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ValueConverter;
import com.nhl.dflib.accumulator.ValueHolder;
import org.apache.commons.csv.CSVRecord;

/**
 * An intermediary holder of CSV values that allows to do conversion and filtering before accumulating values for
 * the DataFrame. This is a flyweight reused for multiple rows.
 *
 * @since 0.8
 */
public class CsvCell<T> {

    private ValueConverter<String, T> converter;
    private ValueHolder<T> holder;
    private int csvColumnPosition;

    public CsvCell(ValueConverter<String, T> converter, ValueHolder<T> holder, int csvColumnPosition) {
        this.converter = converter;
        this.holder = holder;
        this.csvColumnPosition = csvColumnPosition;
    }

    public void set(CSVRecord record) {
        converter.convertAndStore(record.get(csvColumnPosition), holder);
    }

    public void store(Accumulator<T> accumulator) {
        holder.store(accumulator);
    }

    public void store(int pos, Accumulator<T> accumulator) {
        holder.store(pos, accumulator);
    }

    public T get() {
        return holder.get();
    }
}
