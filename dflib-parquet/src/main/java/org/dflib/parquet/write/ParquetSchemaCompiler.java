package org.dflib.parquet.write;

import org.apache.parquet.schema.LogicalTypeAnnotation;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.PrimitiveType;
import org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName;
import org.apache.parquet.schema.Type;
import org.apache.parquet.schema.Type.Repetition;
import org.dflib.parquet.TimeUnit;

import java.util.ArrayList;
import java.util.List;

import static org.apache.parquet.schema.LogicalTypeAnnotation.*;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.BINARY;
import static org.apache.parquet.schema.PrimitiveType.PrimitiveTypeName.FIXED_LEN_BYTE_ARRAY;
import static org.apache.parquet.schema.Type.Repetition.OPTIONAL;
import static org.apache.parquet.schema.Types.primitive;

class ParquetSchemaCompiler {

    private static final String DEFAULT_NAME = "DataFrame";

    private final WriteConfiguration writeConfiguration;

    public ParquetSchemaCompiler(WriteConfiguration writeConfiguration) {
        this.writeConfiguration = writeConfiguration;
    }

    public MessageType compileSchema(DataFrameSchema dataFrameSchema) {
        List<Type> fields = new ArrayList<>();

        for (ColumnMeta column : dataFrameSchema.getColumns()) {
            Type field = createSchemaField(column);
            fields.add(field);
        }
        return new MessageType(DEFAULT_NAME, fields);
    }

    private Type createSchemaField(ColumnMeta column) {

        String columnName = column.getColumnName();
        if (column.isEnum()) {
            return primitive(BINARY, OPTIONAL).as(enumType()).named(columnName);
        }

        String name = column.getInferredTypeName();
        switch (name) {
        case "int":
            return primitive(PrimitiveTypeName.INT32, Repetition.REQUIRED).named(columnName);
        case "java.lang.Integer":
            return primitive(PrimitiveTypeName.INT32, OPTIONAL).named(columnName);

        case "long":
            return primitive(PrimitiveTypeName.INT64, Repetition.REQUIRED).named(columnName);
        case "java.lang.Long":
            return primitive(PrimitiveTypeName.INT64, OPTIONAL).named(columnName);

        case "java.lang.Byte":
            return primitive(PrimitiveTypeName.INT32, OPTIONAL).as(intType(8, true)).named(columnName);
        case "java.lang.Short":
            return primitive(PrimitiveTypeName.INT32, OPTIONAL).as(intType(16, true)).named(columnName);
        case "java.lang.Float":
            return primitive(PrimitiveTypeName.FLOAT, OPTIONAL).named(columnName);

        case "double":
            return primitive(PrimitiveTypeName.DOUBLE, Repetition.REQUIRED).named(columnName);
        case "java.lang.Double":
            return primitive(PrimitiveTypeName.DOUBLE, OPTIONAL).named(columnName);

        case "boolean":
            return primitive(PrimitiveTypeName.BOOLEAN, Repetition.REQUIRED).named(columnName);
        case "java.lang.Boolean":
            return primitive(PrimitiveTypeName.BOOLEAN, OPTIONAL).named(columnName);

        case "java.lang.String":
            return primitive(BINARY, OPTIONAL).as(stringType()).named(columnName);

        case "java.util.UUID":
            return primitive(FIXED_LEN_BYTE_ARRAY, OPTIONAL).as(uuidType())
                    .length(UUIDLogicalTypeAnnotation.BYTES)
                    .named(columnName);
        case "java.time.LocalDate":
            return primitive(PrimitiveTypeName.INT32, OPTIONAL).as(dateType()).named(columnName);
        case "java.time.LocalTime":
            return localTime(columnName, writeConfiguration.timeUnit());
        case "java.time.LocalDateTime":
            return localDateTime(columnName, writeConfiguration.timeUnit());
        case "java.time.Instant":
            return instant(columnName, writeConfiguration.timeUnit());

        case "java.math.BigDecimal":
            return decimalTypeItem(columnName, writeConfiguration.decimalConfig());
        default:
            throw new RuntimeException(name + " not supported in Parquet");
        }
    }

    private PrimitiveType localTime(String columnName, TimeUnit timeUnit) {
        var timeType = timeType(false, toParquetTimeUnit(timeUnit));
        var typeName = PrimitiveTypeName.INT64;
        if (timeUnit == TimeUnit.MILLIS) {
            typeName = PrimitiveTypeName.INT32;
        }
        return primitive(typeName, OPTIONAL).as(timeType).named(columnName);
    }

    private PrimitiveType localDateTime(String columnName, TimeUnit timeUnit) {
        var timeStampType = timestampType(false, toParquetTimeUnit(timeUnit));
        return primitive(PrimitiveTypeName.INT64, OPTIONAL).as(timeStampType).named(columnName);
    }

    private PrimitiveType instant(String columnName, TimeUnit timeUnit) {
        var timeStampType = timestampType(true, toParquetTimeUnit(timeUnit));
        return primitive(PrimitiveTypeName.INT64, OPTIONAL).as(timeStampType).named(columnName);
    }

    private static LogicalTypeAnnotation.TimeUnit toParquetTimeUnit(TimeUnit timeUnit) {
        switch (timeUnit) {
        case MILLIS:
            return LogicalTypeAnnotation.TimeUnit.MILLIS;
        case MICROS:
            return LogicalTypeAnnotation.TimeUnit.MICROS;
        case NANOS:
            return LogicalTypeAnnotation.TimeUnit.NANOS;
        default:
            return LogicalTypeAnnotation.TimeUnit.MICROS;
        }
    }

    private Type decimalTypeItem(String name, DecimalConfig decimalConfig) {
        if (decimalConfig == null) {
            throw new RuntimeException("If BigDecimall is used, a Default Decimal configuration "
                    + "must be provided in the setup of ParquetSaver");
        }
        var decimalType = decimalType(decimalConfig.scale(), decimalConfig.precision());
        if (decimalConfig.precision() <= 9) {
            return primitive(PrimitiveTypeName.INT32, OPTIONAL).as(decimalType).named(name);
        }
        if (decimalConfig.precision() <= 18) {
            return primitive(PrimitiveTypeName.INT64, OPTIONAL).as(decimalType).named(name);
        }
        return primitive(PrimitiveTypeName.BINARY, OPTIONAL).as(decimalType).named(name);
    }
}
