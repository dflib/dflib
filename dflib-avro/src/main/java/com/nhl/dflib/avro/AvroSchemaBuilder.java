package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.avro.types.AvroTypeExtensions;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;

/**
 * Creates Avro Schema from the DataFrame structure.
 *
 * @since 0.11
 */
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

    public Schema compileSchema(DataFrame df) {

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
        validateColumnName(column);
        builder.name(column).type(createColumnSchema(type)).noDefault();
    }

    protected Schema createColumnSchema(Class<?> type) {

        String name = type.isArray() ? type.getComponentType().getName() + "[]" : type.getName();

        switch (name) {

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

            case "byte[]":
            case "java.nio.ByteBuffer":
                return Schema.createUnion(
                        AvroTypeExtensions.BYTE_ARRAY_TYPE.addToSchema(Schema.create(Schema.Type.BYTES)),
                        Schema.create(Schema.Type.NULL));

            case "java.lang.String":
                return Schema.createUnion(
                        AvroTypeExtensions.STRING_TYPE.addToSchema(Schema.create(Schema.Type.STRING)),
                        Schema.create(Schema.Type.NULL));

            case "java.time.LocalDate":
                return Schema.createUnion(
                        AvroTypeExtensions.LOCAL_DATE_TYPE.addToSchema(Schema.create(Schema.Type.INT)),
                        Schema.create(Schema.Type.NULL));

            // TODO: enum, java.time, BigDecimal, BigInteger, etc.

            default:
                return Schema.createUnion(Schema.create(Schema.Type.STRING), Schema.create(Schema.Type.NULL));
        }
    }

    // Making sure the name corresponds to the Avro spec restrictions. Doing it here (instead of deferring to Avro)
    // to provide a user-friendly message. See https://avro.apache.org/docs/current/spec.html#names
    protected void validateColumnName(String name) {

        int length = name.length();
        if (length == 0) {
            throw new RuntimeException("Empty column name");
        }

        char first = name.charAt(0);
        if (!(Character.isLetter(first) || first == '_')) {
            throw new RuntimeException("Column name can not be used as an Avro field name. Name: '" + name + "', invalid first char: " + first);
        }

        for (int i = 1; i < length; i++) {
            char c = name.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '_')) {
                throw new RuntimeException("Column name can not be used as an Avro field name. Name: '" + name + "', invalid char: " + c);
            }
        }

    }
}
