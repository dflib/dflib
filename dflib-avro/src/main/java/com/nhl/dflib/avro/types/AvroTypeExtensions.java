package com.nhl.dflib.avro.types;

import com.nhl.dflib.avro.Avro;

/**
 * @since 0.11
 */
public class AvroTypeExtensions {

    // exposing this catch-all conversion, as we'll need to do some dirty state reset tricks in the AvroLoader
    public static final UnmappedConversion UNMAPPED_CONVERSION = new UnmappedConversion(Object.class);

    static {
        init();
    }

    public static void init() {

        // TODO: enum, java.time, BigDecimal, BigInteger, etc.
        Avro.registerCustomType(new ByteArrayConversion());
        Avro.registerCustomType(new LocalDateConversion());
        Avro.registerCustomType(new LocalDateTimeConversion());
        Avro.registerCustomType(new LocalTimeConversion());
        Avro.registerCustomType(UNMAPPED_CONVERSION);
    }

    /**
     * Invoked from loaders and savers to ensure that DFLib Avro types extensions are loaded in the environment.
     * This method can be safely called multiple times.
     */
    public static void initIfNeeded() {
        // Noop with a side effect... By calling this method the caller could trigger class loading,
        // which in turn would call "init()"
    }
}
