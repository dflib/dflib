package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import org.apache.avro.Conversion;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;

/**
 * Creates Avro Schema from DataFrame structure.
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

            // 1. Match Avro types that require no conversion. Distinguish between primitive
            // (guaranteed non-nullable) types and object wrappers

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

            // 2. Try to find a conversion to a "logical type"
            default:
                Schema schema = convertibleLogicalTypeSchemaOrDefault(type);
                return Schema.createUnion(schema, Schema.create(Schema.Type.NULL));
        }
    }

    protected Schema convertibleLogicalTypeSchemaOrDefault(Class<?> type) {
        Conversion<?> c = GenericData.get().getConversionByClass(type);
        return c != null
                ? c.getRecommendedSchema()
                // TODO: doesn't look like a good default... Should we throw instead? Or use BINARY and convert to byte[] ?
                : Schema.create(Schema.Type.STRING);
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
