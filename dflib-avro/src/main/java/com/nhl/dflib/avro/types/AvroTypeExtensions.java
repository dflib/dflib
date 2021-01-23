package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalTypes;
import org.apache.avro.generic.GenericData;

/**
 * @since 0.11
 */
public class AvroTypeExtensions {

    static {
        // TODO: enum, java.time, BigDecimal, BigInteger, etc.

        registerCustomType(new ByteArrayConversion());
        registerCustomType(new LocalDateConversion());
        registerCustomType(new LocalDateTimeConversion());
        registerCustomType(new StringConversion());
    }

    /**
     * Invoked from loaders and savers to ensure that DFLib Avro types extensions are loaded in the environment.
     * This method can be safely called multiple times.
     */
    public static void init() {
        // Noop with a side effect... By calling this method the caller could trigger class loading,
        // which would execute the above "static" section exactly once.
    }

    /**
     * Registers a custom conversion that works over a give standard Avro type.
     */
    public static void registerCustomType(SingleSchemaConversion<?> conversion) {
        LogicalTypes.LogicalTypeFactory typeFactory = new SingletonLogicalTypeFactory(conversion.getLogicalType());
        LogicalTypes.register(conversion.getLogicalTypeName(), typeFactory);
        GenericData.get().addLogicalTypeConversion(conversion);
    }
}
