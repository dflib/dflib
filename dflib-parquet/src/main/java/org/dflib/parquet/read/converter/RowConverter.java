package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Converter;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type;

import java.util.Arrays;
import java.util.function.Consumer;

import static org.apache.parquet.schema.LogicalTypeAnnotation.*;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.*;



public class RowConverter extends GroupConverter {

    private final Converter[] converters;
    private final Consumer<Object[]> rowConsumer;
    private final Object[] row;

    public RowConverter(GroupType schema, Consumer<Object[]> rowConsumer) {
        this.rowConsumer = rowConsumer;
        this.converters = new Converter[schema.getFields().size()];
        this.row = new Object[schema.getFields().size()];
        int cont = 0;
        for (var schemaField : schema.getFields()) {
            converters[cont] = converterFor(cont, schemaField, row);
            cont++;
        }
    }

    private Converter converterFor(int idx, Type schemaField, Object[] row) {
        Consumer<Object> consumer = value -> row[idx] = value;
        Converter converter = buildFromLogicalTypeConverter(schemaField, consumer);
        if (converter != null) {
            return converter;
        }
        if (schemaField.isPrimitive()) {
            return buildPrimitiveConverters(schemaField, consumer);
        }
        throw new RuntimeException(schemaField.asGroupType().getName() + " deserialization not supported");
    }

    private static Converter buildPrimitiveConverters(Type parquetField, Consumer<Object> consumer) {
        PrimitiveTypeName type = parquetField.asPrimitiveType().getPrimitiveTypeName();
        switch (type) {
        case INT32:
        case INT64:
        case FLOAT:
        case DOUBLE:
        case BOOLEAN:
            return new ToPrimitiveTypeConverter(consumer);
        default:
            throw new RuntimeException(type + " deserialization not supported");
        }
    }

    public static Converter buildFromLogicalTypeConverter(Type parquetField, Consumer<Object> consumer) {
        var logicalTypeAnnotation = parquetField.getLogicalTypeAnnotation();
        if (logicalTypeAnnotation == null) {
            return null;
        }
        var primitiveTypeName = parquetField.asPrimitiveType().getPrimitiveTypeName();
        if (logicalTypeAnnotation.equals(stringType())) {
            return new StringConverter(consumer);
        }
        if (logicalTypeAnnotation.equals(enumType())) {
            return new StringConverter(consumer);
        }
        if (logicalTypeAnnotation instanceof IntLogicalTypeAnnotation) {
            IntLogicalTypeAnnotation intType = (IntLogicalTypeAnnotation) logicalTypeAnnotation;
            if (intType.getBitWidth() == 8) {
                return new ToByteConverter(consumer);
            }
            if (intType.getBitWidth() == 16) {
                return new ToShortConverter(consumer);
            }
        }
        if (logicalTypeAnnotation.equals(uuidType())
                && primitiveTypeName == FIXED_LEN_BYTE_ARRAY) {
            return new UuidConverter(consumer);
        }
        if (logicalTypeAnnotation.equals(dateType())
                && primitiveTypeName == INT32) {
            return new LocalDateConverter(consumer);
        }
        if (logicalTypeAnnotation instanceof TimeLogicalTypeAnnotation
                && (primitiveTypeName == INT32 || primitiveTypeName == INT64)) {
            TimeLogicalTypeAnnotation time = (TimeLogicalTypeAnnotation) logicalTypeAnnotation;
            return new LocalTimeConverter(consumer, time.getUnit());
        }
        if (logicalTypeAnnotation instanceof TimestampLogicalTypeAnnotation
                && primitiveTypeName == INT64) {
            TimestampLogicalTypeAnnotation timeStamp = (TimestampLogicalTypeAnnotation) logicalTypeAnnotation;
            if (timeStamp.isAdjustedToUTC()) {
                return new InstantConverter(consumer, timeStamp.getUnit());
            }
            return new LocalDateTimeConverter(consumer, timeStamp.getUnit());
        }
        if (logicalTypeAnnotation instanceof DecimalLogicalTypeAnnotation) {
            DecimalLogicalTypeAnnotation decimalType = (DecimalLogicalTypeAnnotation) logicalTypeAnnotation;
            return new DecimalConverter(consumer, primitiveTypeName, decimalType.getScale());
        }
        return null;
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
