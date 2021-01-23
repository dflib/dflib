package com.nhl.dflib.avro.types;

import com.nhl.dflib.avro.Avro;

/**
 * @since 0.11
 */
public class AvroTypeExtensions {

    static {

        // TODO: enum, java.time, BigDecimal, BigInteger, etc.

        Avro.registerCustomType(new ByteArrayConversion());
        Avro.registerCustomType(new LocalDateConversion());
        Avro.registerCustomType(new LocalDateTimeConversion());
        Avro.registerCustomType(new LocalTimeConversion());
    }

    /**
     * Invoked from loaders and savers to ensure that DFLib Avro types extensions are loaded in the environment.
     * This method can be safely called multiple times.
     */
    public static void init() {
        // Noop with a side effect... By calling this method the caller could trigger class loading,
        // which would execute the above "static" section exactly once.
    }
}
