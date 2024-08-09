package org.dflib.parquet.write;

import static org.dflib.parquet.write.InstantWrite.getInstantMapper;
import static org.dflib.parquet.write.LocalDateTimeWrite.getLocalDateTimeMapper;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.RecordConsumer;
import org.dflib.parquet.write.InstantWrite.InstantToLong;
import org.dflib.parquet.write.LocalDateTimeWrite.LocalDateTimeToLong;
import org.dflib.row.RowProxy;

class RowWriter {

    private final RecordConsumer recordConsumer;
    private final WriteConfiguration writeConfiguration;
    private final List<Consumer<RowProxy>> fieldWriters = new ArrayList<>();

    public RowWriter(RecordConsumer recordConsumer, WriteConfiguration writeConfiguration, DataFrameSchema schema) {
        this.recordConsumer = recordConsumer;
        this.writeConfiguration = writeConfiguration;

        for (ColumnMeta column : schema.getColumns()) {
            Consumer<RowProxy> writer = buildWriter(column);
            fieldWriters.add(writer);
        }
    }

    public void write(RowProxy record) {
        recordConsumer.startMessage();
        for (var fieldWriter : fieldWriters) {
            fieldWriter.accept(record);
        }
        recordConsumer.endMessage();
    }

    private Consumer<RowProxy> buildWriter(ColumnMeta column) {
        String name = column.getInferredTypeName();
        int idx = column.getIndex();
        switch (name) {
        case "int":
            return new PrimitiveFieldWriter(column, row -> recordConsumer.addInteger(row.getInt(idx)));
        case "java.lang.Integer":
            return new FieldWriter(column, v -> recordConsumer.addInteger(((Integer) v)));
        case "long":
            return new PrimitiveFieldWriter(column, row -> recordConsumer.addLong(row.getLong(idx)));
        case "java.lang.Long":
            return new FieldWriter(column, v -> recordConsumer.addLong(((Long) v)));

        case "java.lang.Byte":
        case "java.lang.Short":
            return new FieldWriter(column, v -> recordConsumer.addInteger(((Number) v).intValue()));

        case "java.lang.Float":
            return new FieldWriter(column, v -> recordConsumer.addFloat((Float) v));
        case "double":
            return new PrimitiveFieldWriter(column, row -> recordConsumer.addDouble(row.getDouble(idx)));
        case "java.lang.Double":
            return new FieldWriter(column, v -> recordConsumer.addDouble((Double) v));

        case "boolean":
            return new PrimitiveFieldWriter(column, row -> recordConsumer.addBoolean(row.getBool(idx)));
        case "java.lang.Boolean":
            return new FieldWriter(column, v -> recordConsumer.addBoolean((Boolean) v));

        case "java.lang.String":
            return new FieldWriter(column, v -> recordConsumer.addBinary(Binary.fromString((String) v)));

        case "java.util.UUID":
            return new FieldWriter(column, v -> recordConsumer.addBinary(uuidToBinary(v)));

        case "java.math.BigDecimal":
            BigDecimalWrite bigDecimalWrite = new BigDecimalWrite(writeConfiguration.getDecimalConfig());
            return new FieldWriter(column, v -> bigDecimalWrite.write(recordConsumer, v));

        case "java.time.LocalDate":
            return new FieldWriter(column, v -> recordConsumer.addInteger((int) ((LocalDate) v).toEpochDay()));
        case "java.time.LocalTime":
            switch (writeConfiguration.getTimeUnit()) {
            case MILLIS:
                return new FieldWriter(column,
                        v -> recordConsumer.addInteger((int) (((LocalTime) v).toNanoOfDay() / 1_000_000L)));
            case MICROS:
                return new FieldWriter(column, v -> recordConsumer.addLong(((LocalTime) v).toNanoOfDay() / 1_000L));
            case NANOS:
                return new FieldWriter(column, v -> recordConsumer.addLong(((LocalTime) v).toNanoOfDay()));
            default:
                throw new IllegalArgumentException("Invalid " + writeConfiguration.getTimeUnit());
            }
        case "java.time.LocalDateTime":
            LocalDateTimeToLong localDateMapper = getLocalDateTimeMapper(writeConfiguration.getTimeUnit());
            return new FieldWriter(column, v -> recordConsumer.addLong(localDateMapper.map((LocalDateTime) v)));
        case "java.time.Instant":
            InstantToLong instantMapper = getInstantMapper(writeConfiguration.getTimeUnit());
            return new FieldWriter(column, v -> recordConsumer.addLong(instantMapper.map((Instant) v)));

        default:
            if (column.isEnum()) {
                return new FieldWriter(column, v -> recordConsumer.addBinary(Binary.fromString(((Enum<?>) v).name())));
            }
            throw new IllegalArgumentException("Unsupported type " + name);
        }
    }

    private class PrimitiveFieldWriter implements Consumer<RowProxy> {

        private final String fieldName;
        private final int idx;
        private final Consumer<RowProxy> rowConsumer;

        PrimitiveFieldWriter(ColumnMeta column, Consumer<RowProxy> rowConsumer) {
            this.fieldName = column.getColumnName();
            this.idx = column.getIndex();
            this.rowConsumer = rowConsumer;
        }

        @Override
        public void accept(RowProxy rowProxy) {
            recordConsumer.startField(fieldName, idx);
            rowConsumer.accept(rowProxy);
            recordConsumer.endField(fieldName, idx);
        }
    }

    private class FieldWriter implements Consumer<RowProxy> {

        private final String fieldName;
        private final int idx;
        private final Consumer<Object> valueConsumer;

        FieldWriter(ColumnMeta column, Consumer<Object> valueConsumer) {
            this.fieldName = column.getColumnName();
            this.idx = column.getIndex();
            this.valueConsumer = valueConsumer;
        }

        @Override
        public void accept(RowProxy rowProxy) {
            Object value = rowProxy.get(idx);
            if (value != null) {
                recordConsumer.startField(fieldName, idx);
                valueConsumer.accept(value);
                recordConsumer.endField(fieldName, idx);
            }
        }
    }

    private static Binary uuidToBinary(Object value) {
        UUID uuid = (UUID) value;
        byte[] arr = new byte[16];
        ByteBuffer bb = ByteBuffer.wrap(arr);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return Binary.fromConstantByteArray(arr);
    }

}
