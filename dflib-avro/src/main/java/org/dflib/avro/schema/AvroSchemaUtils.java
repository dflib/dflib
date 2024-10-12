package org.dflib.avro.schema;

import org.dflib.avro.types.AvroTypeExtensions;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;


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

    public static <T extends Enum<T>> Class<T> knownEnumType(Schema schema) {
        if (!isEnum(schema)) {
            return null;
        }

        String type = schema.getProp(AvroSchemaCompiler.PROPERTY_DFLIB_ENUM_TYPE);
        if (type == null) {
            return null;
        }

        return enumTypeForName(type);
    }

    public static boolean isUnmapped(Schema schema) {

        if (schema == null) {
            return false;
        }

        LogicalType t = schema.getLogicalType();
        return t != null && t.getName().equals(AvroTypeExtensions.UNMAPPED_TYPE.getName());
    }

    private static <T extends Enum<T>> Class<T> enumTypeForName(String name) {
        try {
            return (Class<T>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
