package com.nhl.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.Schema;

import java.util.Objects;

/**
 * @param <T>
 * @since 0.11
 */
public abstract class SingleSchemaConversion<T> extends Conversion<T> {

    private final SingleSchemaLogicalType logicalType;

    protected SingleSchemaConversion(String name, Schema.Type avroType) {
        this(new SingleSchemaLogicalType(name, avroType));
    }

    protected SingleSchemaConversion(SingleSchemaLogicalType logicalType) {
        this.logicalType = Objects.requireNonNull(logicalType);
    }

    @Override
    public String getLogicalTypeName() {
        return logicalType.getName();
    }

    @Override
    public Schema getRecommendedSchema() {
        return logicalType.getRecommendedSchema();
    }

    public SingleSchemaLogicalType getLogicalType() {
        return logicalType;
    }
}
