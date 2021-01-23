package com.nhl.dflib.avro.schema;

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
public class AvroSchemaCompiler {

    private static final String DEFAULT_NAME = "DataFrame";
    private static final String DEFAULT_NAMESPACE = "com.nhl.dflib";

    protected String name;
    protected String namespace;

    /**
     * Sets the schema name of the generated Avro file. Optional. The default will be "DataFrame".
     */
    public AvroSchemaCompiler name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the schema namespace of the generated Avro file. Optional. The default will be "com.nhl.dflib".
     */
    public AvroSchemaCompiler namespace(String namespace) {
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
                return nullableSchema(Schema.create(Schema.Type.INT));

            case "long":
                return Schema.create(Schema.Type.LONG);
            case "java.lang.Long":
                return nullableSchema(Schema.create(Schema.Type.LONG));

            case "float":
                return Schema.create(Schema.Type.FLOAT);
            case "java.lang.Float":
                return nullableSchema(Schema.create(Schema.Type.FLOAT));

            case "double":
                return Schema.create(Schema.Type.DOUBLE);
            case "java.lang.Double":
                return nullableSchema(Schema.create(Schema.Type.DOUBLE));

            case "boolean":
                return Schema.create(Schema.Type.BOOLEAN);
            case "java.lang.Boolean":
                return nullableSchema(Schema.create(Schema.Type.BOOLEAN));

            // 2. String is special. It requires no conversion, but does require a special schema property to be handled
            // as String and not org.apache.avro.util.Utf8
            case "java.lang.String":
                return nullableSchema(createStringSchema());

            // 3. Try to find a conversion to a "logical type"
            default:
                Schema schema = convertibleLogicalTypeSchema(type);
                return nullableSchema(schema != null ? schema : defaultValueSchema(type));
        }
    }

    protected Schema createStringSchema() {
        Schema schema = Schema.create(Schema.Type.STRING);
        GenericData.setStringType(schema, GenericData.StringType.String);
        return schema;
    }

    protected Schema convertibleLogicalTypeSchema(Class<?> type) {
        Conversion<?> c = GenericData.get().getConversionByClass(type);
        return c != null
                ? c.getRecommendedSchema()
                // TODO: doesn't look like a good default... Should we throw instead? Or use BINARY and convert to byte[] ?
                : Schema.create(Schema.Type.STRING);
    }

    protected Schema defaultValueSchema(Class<?> type) {
        // TODO: doesn't look like a good default... Should we throw instead? Or use BINARY and convert to byte[] ?
        return Schema.create(Schema.Type.STRING);
    }

    protected Schema nullableSchema(Schema schema) {
        return Schema.createUnion(schema, Schema.create(Schema.Type.NULL));
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
