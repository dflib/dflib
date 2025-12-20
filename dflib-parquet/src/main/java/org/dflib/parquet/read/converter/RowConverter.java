package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.Type;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class RowConverter extends GroupConverter {

    private final Converter[] converters;
    private final Consumer<Object[]> rowConsumer;
    private final Object[] row;

    public RowConverter(GroupType schema, Consumer<Object[]> rowConsumer) {
        this.rowConsumer = rowConsumer;

        List<Type> fields = schema.getFields();
        int len = fields.size();

        this.converters = new Converter[len];
        this.row = new Object[len];

        for (int i = 0; i < len; i++) {
            converters[i] = converter(fields.get(i), row, i);
        }
    }

    private Converter converter(Type type, Object[] row, int i) {
        Consumer<Object> consumer = value -> row[i] = value;
        return Converters.converter(type, consumer);
    }

    @Override
    public Converter getConverter(int fieldIndex) {
        return converters[fieldIndex];
    }

    @Override
    public void start() {
        Arrays.fill(row, null);
    }

    @Override
    public void end() {
        rowConsumer.accept(row);
    }

}
