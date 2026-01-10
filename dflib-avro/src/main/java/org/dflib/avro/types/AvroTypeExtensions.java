package org.dflib.avro.types;

import org.apache.avro.Conversion;
import org.apache.avro.Conversions;
import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;
import org.apache.avro.data.TimeConversions;
import org.apache.avro.generic.GenericData;
import org.dflib.avro.write.GenericDataForSave;


public class AvroTypeExtensions {

    private static final GenericData GENERIC_DATA_FOR_SAVE;
    private static final GenericData GENERIC_DATA_FOR_LOAD;

    static {

        GENERIC_DATA_FOR_SAVE = new GenericDataForSave();
        GENERIC_DATA_FOR_LOAD = new GenericData();

        // TODO: saver also should have these... use "registerConversion" , write tests for UUID, Instant, etc.
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new Conversions.DecimalConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new Conversions.UUIDConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new Conversions.DurationConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.TimestampMillisConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.TimestampMicrosConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new TimeConversions.TimestampNanosConversion());


        registerConversion(new Conversions.BigDecimalConversion());
        registerConversion(new TimeConversions.DateConversion());
        registerConversion(new TimeConversions.LocalTimestampMillisConversion());
        registerConversion(new TimeConversions.LocalTimestampMicrosConversion());
        registerConversion(new TimeConversions.LocalTimestampNanosConversion());
        registerConversion(new TimeConversions.TimeMicrosConversion());
        registerConversion(new TimeConversions.TimeMillisConversion());

        // custom DFLib logical types
        registerConversion(new BigIntegerConversion());
        registerConversion(new DurationConversion());
        registerConversion(new PeriodConversion());
        registerConversion(new YearMonthConversion());
        registerConversion(new YearConversion());
        registerConversion(new UnmappedConversion());

        registerLogicalType(BigIntegerConversion.TYPE);
        registerLogicalType(DurationConversion.TYPE);
        registerLogicalType(PeriodConversion.TYPE);
        registerLogicalType(YearMonthConversion.TYPE);
        registerLogicalType(YearConversion.TYPE);
        registerLogicalType(UnmappedConversion.TYPE);

        configureLegacyDFLibV1Extensions();
    }

    private static void configureLegacyDFLibV1Extensions() {
        registerLogicalType(BigDecimalConversion.TYPE);
        registerLogicalType(ByteArrayConversion.TYPE);
        registerLogicalType(LocalDateConversion.TYPE);
        registerLogicalType(LocalTimeConversion.TYPE);
        registerLogicalType(LocalDateTimeConversion.TYPE);

        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new BigDecimalConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new ByteArrayConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new LocalDateConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new LocalTimeConversion());
        GENERIC_DATA_FOR_LOAD.addLogicalTypeConversion(new LocalDateTimeConversion());
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
