package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

public class AvroSchemaBuilder {

    private static final String DEFAULT_NAME = "DataFrame";
    private static final String DEFAULT_NAMESPACE = "com.nhl.dflib";

    protected String name;
    protected String namespace;

    /**
     * Sets the schema name of the generated Avro file. Optional. The default will be "DataFrame".
     */
    public AvroSchemaBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the schema namespace of the generated Avro file. Optional. The default will be "com.nhl.dflib".
     */
    public AvroSchemaBuilder namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public Schema createSchema(DataFrame df) {

        String name = this.name != null ? this.name : DEFAULT_NAME;
        String namespace = this.namespace != null ? this.namespace : DEFAULT_NAMESPACE;

        SchemaBuilder.FieldAssembler<Schema> fields = SchemaBuilder
                .record(name)
                .namespace(namespace)
                .fields();

        for (String column : df.getColumnsIndex()) {
            Class<?> type = df.getColumn(column).getInferredType();
            createSchemaField(fields, column, type);
        }

        return fields.endRecord();
    }

    protected void createSchemaField(SchemaBuilder.FieldAssembler<Schema> builder, String column, Class<?> type) {
        builder.name(column).type(createColumnSchema(type)).noDefault();
    }

    protected Schema createColumnSchema(Class<?> type) {
        switch (type.getName()) {

            case "int":
                return Schema.create(Schema.Type.INT);
            case "java.lang.Integer":
                return Schema.createUnion(Schema.create(Schema.Type.INT), Schema.create(Schema.Type.NULL));

            case "long":
                return Schema.create(Schema.Type.LONG);
            case "java.lang.Long":
                return Schema.createUnion(Schema.create(Schema.Type.LONG), Schema.create(Schema.Type.NULL));

            case "float":
                return Schema.create(Schema.Type.FLOAT);
            case "java.lang.Float":
                return Schema.createUnion(Schema.create(Schema.Type.FLOAT), Schema.create(Schema.Type.NULL));

            case "double":
                return Schema.create(Schema.Type.DOUBLE);
            case "java.lang.Double":
                return Schema.createUnion(Schema.create(Schema.Type.DOUBLE), Schema.create(Schema.Type.NULL));

            case "boolean":
                return Schema.create(Schema.Type.BOOLEAN);
            case "java.lang.Boolean":
                return Schema.createUnion(Schema.create(Schema.Type.BOOLEAN), Schema.create(Schema.Type.NULL));

            // TODO: enum, byte[], java.time, BigDecimal, BigInteger, etc.

            default:
                return Schema.createUnion(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.NULL));
        }
    }
}
