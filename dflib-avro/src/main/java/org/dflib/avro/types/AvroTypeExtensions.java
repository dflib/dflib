package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.data.TimeConversions;
import org.apache.avro.generic.GenericData;


public class AvroTypeExtensions {

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

        registerLogicalType(BigDecimalConversion.TYPE);
        registerLogicalType(BigIntegerConversion.TYPE);
        registerLogicalType(ByteArrayConversion.TYPE);
        registerLogicalType(DurationConversion.TYPE);
        registerLogicalType(LocalDateConversion.TYPE);
        registerLogicalType(LocalDateTimeConversion.TYPE);
        registerLogicalType(LocalTimeConversion.TYPE);
        registerLogicalType(PeriodConversion.TYPE);
        registerLogicalType(YearMonthConversion.TYPE);
        registerLogicalType(YearConversion.TYPE);
        registerLogicalType(UnmappedConversion.TYPE);

        registerConversion(new BigDecimalConversion());
        registerConversion(new BigIntegerConversion());
        registerConversion(new ByteArrayConversion());
        registerConversion(new DurationConversion());
        registerConversion(new LocalDateConversion());
        registerConversion(new LocalDateTimeConversion());
        registerConversion(new LocalTimeConversion());
        registerConversion(new PeriodConversion());
        registerConversion(new YearMonthConversion());
        registerConversion(new YearConversion());

        registerConversion(new UnmappedConversion());
    }

    /**
     * @since 2.0.0
     */
    public static void registerLogicalType(LogicalType logicalType) {

        LogicalTypes.LogicalTypeFactory typeFactory = new LogicalTypes.LogicalTypeFactory() {
            @Override
            public LogicalType fromSchema(Schema schema) {
                return logicalType;
            }

            @Override
            public String getTypeName() {
                return logicalType.getName();
            }
        };

        LogicalTypes.register(logicalType.getName(), typeFactory);
    }

    /**
     * An extension point to register a conversion for a custom type mapped to an underlying Avro simple type.
     * DFLib already provides a collection of type extensions to map various common value types to Avro. This
     * method allows to cover the types that are not (yet) included in DFLib.
     *
     * @since 2.0.0
     */
    public static void registerConversion(Conversion<?> conversion) {
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
