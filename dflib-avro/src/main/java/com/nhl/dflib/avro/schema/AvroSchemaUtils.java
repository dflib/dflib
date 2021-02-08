package com.nhl.dflib.avro.schema;

import com.nhl.dflib.avro.types.AvroTypeExtensions;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericEnumSymbol;

import java.util.Optional;

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

    public static boolean isEnum(Schema schema) {
        return schema != null && schema.getType() == Schema.Type.ENUM;
    }

    public static Optional<Class<?>> knownEnumType(Schema schema) {
        if (!isEnum(schema)) {
            return Optional.empty();
        }

        String type = schema.getProp(AvroSchemaCompiler.PROPERTY_DFLIB_ENUM_TYPE);
        if (type == null) {
            return Optional.empty();
        }

        return classForName(type);
    }

    public static boolean isUnmapped(Schema schema) {

        if (schema == null) {
            return false;
        }

        LogicalType t = schema.getLogicalType();
        return t != null && t.getName().equals(AvroTypeExtensions.UNMAPPED_TYPE.getName());
    }

    public static GenericEnumSymbol<?> toEnumSymbol(Object value, Schema schema) {
        return value != null ? new GenericData.EnumSymbol(schema, value) : null;
    }

    public static <T extends Enum<T>> T toEnum(GenericEnumSymbol<?> symbol, Class<?> enumType) {
        if (symbol == null) {
            return null;
        }

        String name = symbol.toString();
        if (!enumType.isEnum()) {
            throw new IllegalArgumentException("'" + enumType + "' is not an enum class");
        }

        return Enum.valueOf((Class<T>) enumType, name);
    }

    private static Optional<Class<?>> classForName(String name) {
        try {
            return Optional.of(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
