package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;


public class SingletonLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {

    private final LogicalType type;

    public SingletonLogicalTypeFactory(LogicalType type) {
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
