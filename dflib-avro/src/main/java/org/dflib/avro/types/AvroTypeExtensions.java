package org.dflib.avro.types;

import org.apache.avro.Conversions;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.data.TimeConversions;
import org.apache.avro.generic.GenericData;


public class AvroTypeExtensions {

    public static final SingleSchemaLogicalType UNMAPPED_TYPE = new SingleSchemaLogicalType("dflib-unmapped", Schema.Type.STRING);

    private static final GenericData GENERIC_DATA_FOR_SAVE;
    private static final GenericData GENERIC_DATA_FOR_LOAD;

    static {

        GENERIC_DATA_FOR_SAVE = new GenericData();

        GENERIC_DATA_FOR_LOAD = new GenericData();

        // add Avro logical types to "load" data so that we can read files produced by other tools
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new Conversions.DecimalConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new Conversions.BigDecimalConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new Conversions.UUIDConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new Conversions.DurationConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.DateConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.TimestampMillisConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.TimestampMicrosConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.TimestampNanosConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.LocalTimestampMillisConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.LocalTimestampMicrosConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.LocalTimestampNanosConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.TimeMicrosConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.TimeMillisConversion());

        registerCustomType(new BigDecimalConversion());
        registerCustomType(new BigIntegerConversion());
        registerCustomType(new ByteArrayConversion());
        registerCustomType(new DurationConversion());
        registerCustomType(new LocalDateConversion());
        registerCustomType(new LocalDateTimeConversion());
        registerCustomType(new LocalTimeConversion());
        registerCustomType(new PeriodConversion());
        registerCustomType(new YearMonthConversion());
        registerCustomType(new YearConversion());

        registerCustomType(new UnmappedConversion(UNMAPPED_TYPE));
    }

    /**
     * An extension point to register a conversion for a custom type mapped to an underlying Avro simple type.
     * DFLib already provides a collection of type extensions to map various common value types to Avro. This
     * method allows to cover the types that are not (yet) included in DFLib.
     *
     * @param conversion a custom subclass of {@link SingleSchemaConversion} implementing custom type read/write logic.
     * @since 2.0
     */
    public static void registerCustomType(SingleSchemaConversion<?> conversion) {
        LogicalTypes.LogicalTypeFactory typeFactory = new SingletonLogicalTypeFactory(conversion.getLogicalType());
        LogicalTypes.register(conversion.getLogicalType().getName(), typeFactory);
        GENERIC_DATA_FOR_SAVE.addLogicalTypeConversion(conversion);
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(conversion);
    }

    /**
     * Invoked from loaders and savers to ensure that DFLib Avro types extensions are loaded in the environment.
     * This method can be safely called multiple times.
     */
    public static void initIfNeeded() {
        // Noop with a side effect... By calling this method the caller could trigger class loading,
        // which in turn would call "init()"
    }

    public static GenericData getGenericDataForLoad() {
        return GENERIC_DATA_FOR_LOAD;
    }

    public static GenericData getGenericDataForSave() {
        return GENERIC_DATA_FOR_SAVE;
    }
}
