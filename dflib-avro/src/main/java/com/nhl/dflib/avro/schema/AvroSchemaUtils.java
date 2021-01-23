package com.nhl.dflib.avro.schema;

import com.nhl.dflib.avro.types.AvroTypeExtensions;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericEnumSymbol;

/**
 * @since 0.11
 */
public class AvroSchemaUtils {

    public static Schema unpackUnion(Schema union) {

        for (Schema child : union.getTypes()) {
            if (!child.isNullable()) {
                return child;
            }
        }

        return null;
    }

    public static boolean isEnumType(Schema schema) {
        return schema != null && schema.getType() == Schema.Type.ENUM;
    }

    public static boolean isUnmappedType(Schema schema) {

        if (schema == null) {
            return false;
        }

        LogicalType t = schema.getLogicalType();
        return t != null && t.getName().equals(AvroTypeExtensions.UNMAPPED_TYPE.getName());
    }

    public static GenericEnumSymbol<?> convertToEnumSymbol(Object value, Schema schema) {
        return value != null ? new GenericData.EnumSymbol(schema, value) : null;
    }

    public static <T extends Enum<T>> T convertToEnum(GenericEnumSymbol<?> symbol) {
        if (symbol == null) {
            return null;
        }

        String name = symbol.toString();
        String type = symbol.getSchema().getProp(AvroSchemaCompiler.PROPERTY_DFLIB_ENUM_TYPE);

        if (type == null) {
            throw new IllegalArgumentException("GenericEnumSymbol doesn't have '"
                    + AvroSchemaCompiler.PROPERTY_DFLIB_ENUM_TYPE
                    + "' schema property and can't be converted to Java enum: " + name);
        }

        Class<?> typeClass = classForName(type);
        if (!typeClass.isEnum()) {
            throw new IllegalArgumentException("'" + type + "' is not an enum class");
        }

        return Enum.valueOf((Class<T>) typeClass, name);
    }

    private static Class<?> classForName(String name) {
        try {
            return Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Invalid to unknown class name: " + name, e);
        }
    }
}
