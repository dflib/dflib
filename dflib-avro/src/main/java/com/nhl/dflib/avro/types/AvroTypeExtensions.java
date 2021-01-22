package com.nhl.dflib.avro.types;

import org.apache.avro.LogicalTypes;
import org.apache.avro.generic.GenericData;

/**
 * @since 0.11
 */
public class AvroTypeExtensions {

    public static final ByteArrayType BYTE_ARRAY_TYPE = new ByteArrayType();
    public static final StringType STRING_TYPE = new StringType();
    public static final LocalDateType LOCAL_DATE_TYPE = new LocalDateType();

    static {
        // static logical types
        LogicalTypes.register(ByteArrayType.NAME, new InstanceLogicalTypeFactory(BYTE_ARRAY_TYPE));
        LogicalTypes.register(StringType.NAME, new InstanceLogicalTypeFactory(STRING_TYPE));
        LogicalTypes.register(LocalDateType.NAME, new InstanceLogicalTypeFactory(LOCAL_DATE_TYPE));

        // static conversions
        GenericData.get().addLogicalTypeConversion(new ByteArrayConversion());
        GenericData.get().addLogicalTypeConversion(new StringConversion());
        GenericData.get().addLogicalTypeConversion(new LocalDateConversion());
    }

    public static void init() {
        // Noop.

        // The point of this method is that the caller could trigger class loading,
        // which would execute the above "static" section exactly once
    }
}
