package com.nhl.dflib.csv.loader;

import com.nhl.dflib.builder.ValueAccum;
import com.nhl.dflib.builder.ValueExtractor;
import com.nhl.dflib.builder.ValueHolder;
import org.apache.commons.csv.CSVRecord;

/**
 * An intermediary holder of CSV values that allows to do conversion and filtering before accumulating values for
 * the DataFrame. This is a flyweight reused for multiple rows.
 *
 * @since 0.8
 */
public class CsvCell<T> {

    private final ValueExtractor<CSVRecord, T> extractor;
    private final ValueHolder<T> holder;

    public CsvCell(ValueExtractor<CSVRecord, T> extractor) {
        this.extractor = extractor;
        this.holder = extractor.createHolder();
    }

    public void set(CSVRecord record) {
        extractor.extract(record, holder);
    }

    public void store(ValueAccum<T> accumulator) {
        holder.pushToStore(accumulator);
    }

    public void store(int pos, ValueAccum<T> accumulator) {
        holder.pushToStore(accumulator, pos);
    }

    public T get() {
        return holder.get();
    }
}
