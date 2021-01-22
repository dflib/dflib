package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

/**
 * @since 0.11
 */
public class InstanceLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {

    private final LogicalType type;

    public InstanceLogicalTypeFactory(LogicalType type) {
        this.type = type;
    }

    @Override
    public LogicalType fromSchema(Schema schema) {
        return type;
    }

    @Override
    public String getTypeName() {
        return type.getName();
    }
}
