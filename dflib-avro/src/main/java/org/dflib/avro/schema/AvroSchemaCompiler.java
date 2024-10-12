package org.dflib.avro.schema;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.avro.types.AvroTypeExtensions;
import org.apache.avro.Conversion;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates Avro Schema from DataFrame structure.
 */
public class AvroSchemaCompiler {

    static {
        AvroTypeExtensions.initIfNeeded();
    }

    public static final String PROPERTY_DFLIB_ENUM_TYPE = "dflib.enum.type";

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroSchemaCompiler.class);

    private static final String DEFAULT_NAME = "DataFrame";
    private static final String DEFAULT_NAMESPACE = "org.dflib";

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
     * Sets the schema namespace of the generated Avro file. Optional. The default will be "org.dflib".
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
            createSchemaField(fields, column, df.getColumn(column));
        }

        return fields.endRecord();
    }

    protected void createSchemaField(SchemaBuilder.FieldAssembler<Schema> builder, String columnName, Series<?> column) {
        validateColumnName(columnName);
        builder.name(columnName).type(createColumnSchema(column)).noDefault();
    }

    protected Schema createColumnSchema(Series<?> column) {

        Class<?> type = column.getInferredType();

        if (type.isEnum()) {
            return makeNullable(enumSchema(type));
        }

        String name = type.isArray() ? type.getComponentType().getName() + "[]" : type.getName();

        switch (name) {

            // 1. Match Avro types that require no conversion. Distinguish between primitive
            // (guaranteed non-nullable) types and object wrappers

            case "int":
                return Schema.create(Schema.Type.INT);
            case "java.lang.Integer":
                return makeNullable(Schema.create(Schema.Type.INT));

            case "long":
                return Schema.create(Schema.Type.LONG);
            case "java.lang.Long":
                return makeNullable(Schema.create(Schema.Type.LONG));

            case "float":
                return Schema.create(Schema.Type.FLOAT);
            case "java.lang.Float":
                return makeNullable(Schema.create(Schema.Type.FLOAT));

            case "double":
                return Schema.create(Schema.Type.DOUBLE);
            case "java.lang.Double":
                return makeNullable(Schema.create(Schema.Type.DOUBLE));

            case "boolean":
                return Schema.create(Schema.Type.BOOLEAN);
            case "java.lang.Boolean":
                return makeNullable(Schema.create(Schema.Type.BOOLEAN));

            case "java.lang.String":
                return makeNullable(Schema.create(Schema.Type.STRING));

            // 3. Try to find a conversion to a "logical type"
            default:
                Schema logicalTypeSchema = logicalTypeSchema(type);
                if (logicalTypeSchema != null) {
                    return makeNullable(logicalTypeSchema);
                }

                Schema nullsOnlySchema = nullsOnlySchema(column);
                if (nullsOnlySchema != null) {
                    return nullsOnlySchema;
                }

                return makeNullable(unmappedValueSchema(type));
        }
    }

    protected Schema enumSchema(Class<?> enumType) {

        String name = enumType.getSimpleName();
        String namespace = enumType.getPackage() != null ? enumType.getPackage().getName() : null;
        String typeName = enumType.getName();

        List<String> values = Arrays.stream(enumType.getEnumConstants())
                .map(Object::toString)
                .collect(Collectors.toList());

        Schema schema = Schema.createEnum(name, namespace, null, values);
        schema.addProp(PROPERTY_DFLIB_ENUM_TYPE, typeName);
        return schema;
    }

    protected Schema logicalTypeSchema(Class<?> type) {
        Conversion<?> c = GenericData.get().getConversionByClass(type);
        return c != null ? c.getRecommendedSchema() : null;
    }

    protected Schema nullsOnlySchema(Series<?> column) {

        // suppose no-data columns should be presented as "unknown", not as "null"
        if (column.size() == 0) {
            return null;
        }

        for (Object o : column) {
            if (o != null) {
                return null;
            }
        }
        return Schema.create(Schema.Type.NULL);
    }

    protected Schema unmappedValueSchema(Class<?> type) {
        LOGGER.warn("Unmapped schema type '{}'. Will use 'toString()' conversion and will deserialize as String", type.getName());
        return AvroTypeExtensions.UNMAPPED_TYPE.getRecommendedSchema();
    }

    protected Schema makeNullable(Schema schema) {
        return schema.getType() == Schema.Type.NULL
                ? schema
                : Schema.createUnion(schema, Schema.create(Schema.Type.NULL));
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
