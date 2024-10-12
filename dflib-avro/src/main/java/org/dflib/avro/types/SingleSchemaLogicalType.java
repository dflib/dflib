package org.dflib.avro.types;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import java.util.Objects;


public class SingleSchemaLogicalType extends LogicalType {

    private final Schema.Type avroType;

    public SingleSchemaLogicalType(String name, Schema.Type avroType) {
        super(name);
        this.avroType = Objects.requireNonNull(avroType);
    }

    public Schema getRecommendedSchema() {
        return addToSchema(Schema.create(avroType));
    }

    @Override
    public void validate(Schema schema) {
        super.validate(schema);

        if (schema.getType() != avroType) {
            throw new IllegalArgumentException(
                    "Logical type '" + getName() + "' must be backed by '" + avroType.getName() + "'");
        }
    }
}
