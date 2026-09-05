package org.dflib.hardwood.write;

import dev.hardwood.metadata.LogicalType;
import dev.hardwood.metadata.PhysicalType;
import dev.hardwood.metadata.RepetitionType;
import dev.hardwood.schema.FileSchema;
import org.dflib.hardwood.TimeUnit;

/**
 * Compiles a DFLib DataFrame schema into a Hardwood {@link FileSchema}.
 *
 * @since 2.0.0
 */
public class SchemaCompiler {

    private static final String DEFAULT_NAME = "DataFrame";
    private static final int UUID_BYTES = 16;

    private final WriteConfiguration writeConfiguration;

    public SchemaCompiler(WriteConfiguration writeConfiguration) {
        this.writeConfiguration = writeConfiguration;
    }

    public FileSchema compileSchema(DataFrameSchema dataFrameSchema) {

        FileSchema.Builder builder = FileSchema.builder(DEFAULT_NAME);
        for (ColumnMeta column : dataFrameSchema.getColumns()) {
            addColumn(builder, column);
        }

        return builder.build();
    }

    private void addColumn(FileSchema.Builder builder, ColumnMeta column) {

        String name = column.getColumnName();

        if (column.isEnum()) {
            builder.addColumn(name, PhysicalType.BYTE_ARRAY, RepetitionType.OPTIONAL, new LogicalType.EnumType());
            return;
        }

        // Primitive DFLib columns can not contain nulls, and are stored as "required" Parquet fields
        RepetitionType optional = RepetitionType.OPTIONAL;
        RepetitionType required = RepetitionType.REQUIRED;

        switch (column.getInferredTypeName()) {

            case "int" -> builder.addColumn(name, PhysicalType.INT32, required);
            case "java.lang.Integer" -> builder.addColumn(name, PhysicalType.INT32, optional);

            case "long" -> builder.addColumn(name, PhysicalType.INT64, required);
            case "java.lang.Long" -> builder.addColumn(name, PhysicalType.INT64, optional);

            case "java.lang.Byte" ->
                    builder.addColumn(name, PhysicalType.INT32, optional, new LogicalType.IntType(8, true));
            case "java.lang.Short" ->
                    builder.addColumn(name, PhysicalType.INT32, optional, new LogicalType.IntType(16, true));

            case "java.lang.Float" -> builder.addColumn(name, PhysicalType.FLOAT, optional);

            case "double" -> builder.addColumn(name, PhysicalType.DOUBLE, required);
            case "java.lang.Double" -> builder.addColumn(name, PhysicalType.DOUBLE, optional);

            case "boolean" -> builder.addColumn(name, PhysicalType.BOOLEAN, required);
            case "java.lang.Boolean" -> builder.addColumn(name, PhysicalType.BOOLEAN, optional);

            case "java.lang.String" ->
                    builder.addColumn(name, PhysicalType.BYTE_ARRAY, optional, new LogicalType.StringType());

            case "java.util.UUID" -> builder.addColumn(
                    name, PhysicalType.FIXED_LEN_BYTE_ARRAY, optional, UUID_BYTES, new LogicalType.UuidType());

            case "java.time.LocalDate" ->
                    builder.addColumn(name, PhysicalType.INT32, optional, new LogicalType.DateType());

            case "java.time.LocalTime" -> localTime(builder, name);
            case "java.time.LocalDateTime" -> builder.addColumn(name, PhysicalType.INT64, optional,
                    new LogicalType.TimestampType(false, timeUnit()));
            case "java.time.Instant" -> builder.addColumn(name, PhysicalType.INT64, optional,
                    new LogicalType.TimestampType(true, timeUnit()));

            case "java.math.BigDecimal" -> decimal(builder, name);

            default -> throw new IllegalArgumentException(
                    column.getInferredTypeName() + " not supported in Parquet");
        }
    }

    private void localTime(FileSchema.Builder builder, String name) {
        TimeUnit unit = writeConfiguration.timeUnit();
        PhysicalType type = unit == TimeUnit.MILLIS ? PhysicalType.INT32 : PhysicalType.INT64;
        builder.addColumn(name, type, RepetitionType.OPTIONAL, new LogicalType.TimeType(false, timeUnit()));
    }

    private void decimal(FileSchema.Builder builder, String name) {

        DecimalConfig config = writeConfiguration.decimalConfig();
        if (config == null) {
            throw new IllegalStateException(
                    "If BigDecimal is used, a decimal configuration must be provided in the setup of HardwoodSaver");
        }

        LogicalType decimal = new LogicalType.DecimalType(config.scale(), config.precision());

        PhysicalType type = config.precision() <= 9
                ? PhysicalType.INT32
                : config.precision() <= 18 ? PhysicalType.INT64 : PhysicalType.BYTE_ARRAY;

        builder.addColumn(name, type, RepetitionType.OPTIONAL, decimal);
    }

    private LogicalType.TimeUnit timeUnit() {
        return switch (writeConfiguration.timeUnit()) {
            case MILLIS -> LogicalType.TimeUnit.MILLIS;
            case MICROS -> LogicalType.TimeUnit.MICROS;
            case NANOS -> LogicalType.TimeUnit.NANOS;
        };
    }
}
