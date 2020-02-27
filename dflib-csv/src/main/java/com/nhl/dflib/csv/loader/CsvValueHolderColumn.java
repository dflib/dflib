package com.nhl.dflib.csv.loader;

import com.nhl.dflib.series.builder.Accumulator;
import com.nhl.dflib.series.builder.ValueConverter;
import com.nhl.dflib.series.builder.ValueHolder;
import org.apache.commons.csv.CSVRecord;

/**
 * @since 0.8
 */
public class CsvValueHolderColumn<T> {

    private ValueConverter<String, T> converter;
    private ValueHolder<T> holder;
    private int csvColumnPosition;

    public CsvValueHolderColumn(ValueConverter<String, T> converter, ValueHolder<T> holder, int csvColumnPosition) {
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

}
