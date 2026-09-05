package org.dflib.parquet.read;

import dev.hardwood.metadata.ConvertedType;
import dev.hardwood.metadata.LogicalType;
import dev.hardwood.metadata.PhysicalType;
import dev.hardwood.metadata.RepetitionType;
import dev.hardwood.reader.ColumnReader;
import dev.hardwood.schema.SchemaNode;
import org.dflib.Series;
import org.dflib.builder.ValueCompactor;

/**
 * Accumulates a Parquet column into a DFLib {@link Series}, a batch at a time.
 *
 * <p>The counterpart of {@link ColumnBuilder}, which reads the same data a row at a time. Row count is known from the
 * file footer before reading starts, so every reader here fills an exactly-sized array and hands it to the Series
 * rather than growing an accumulator. Batch arrays belong to the caller once returned - Hardwood allocates a fresh
 * one per batch - so a column of non-null primitives is a sequence of array copies and nothing else.
 *
 * @since 2.0.0
 */
public interface BatchReader {

    /**
     * Appends the reader's current batch.
     */
    void append(ColumnReader reader);

    Series<?> toSeries();

    /**
     * Whether a column of this schema can be read through the batch API. Nested columns are read a row at a time
     * instead, as assembling lists and maps from Hardwood's layer representation is a separate matter.
     */
    static boolean supports(SchemaNode colSchema) {
        return colSchema instanceof SchemaNode.PrimitiveNode p && p.type() != PhysicalType.INT96;
    }

    static BatchReader of(SchemaNode colSchema, ConvertedType legacyType, int height) {

        SchemaNode.PrimitiveNode p = (SchemaNode.PrimitiveNode) colSchema;
        LogicalType lt = LogicalTypes.effective(p.logicalType(), legacyType);
        boolean allowsNulls = p.repetitionType() != RepetitionType.REQUIRED;

        // Columns that can't be null and map to a DFLib primitive Series are copied straight through
        if (!allowsNulls && isPlainPrimitive(p, lt)) {
            return switch (p.type()) {
                case BOOLEAN -> new BoolBatchReader(height);
                case INT32 -> new IntBatchReader(height);
                case INT64 -> new LongBatchReader(height);
                case FLOAT -> new FloatBatchReader(height);
                case DOUBLE -> new DoubleBatchReader(height);
                default -> throw new IllegalStateException("Unreachable: " + p.type());
            };
        }

        return new ObjectBatchReader(BatchConverter.of(p, lt), height, compacts(lt, p.type()));
    }

    /**
     * Whether the column's values are the physical type as-is, with no logical type to decode. FLOAT16 is included,
     * as a signed 32-bit int and a plain INT32 are the same values; the annotations that are not are the ones that
     * change the Java type DFLib exposes.
     */
    private static boolean isPlainPrimitive(SchemaNode.PrimitiveNode p, LogicalType lt) {
        if (p.type() == PhysicalType.BYTE_ARRAY || p.type() == PhysicalType.FIXED_LEN_BYTE_ARRAY) {
            return false;
        }
        if (lt == null) {
            return true;
        }
        return lt instanceof LogicalType.IntType it && it.isSigned()
                && (it.bitWidth() == 32 || it.bitWidth() == 64);
    }

    /**
     * Whether repeated values are worth replacing with a single shared instance. Strings arrive already shared via
     * Hardwood's dictionary, and byte arrays and intervals are mutable and lack value equality.
     */
    private static boolean compacts(LogicalType lt, PhysicalType type) {
        if (lt == null) {
            return type != PhysicalType.BYTE_ARRAY && type != PhysicalType.FIXED_LEN_BYTE_ARRAY;
        }
        return !(lt instanceof LogicalType.StringType
                || lt instanceof LogicalType.EnumType
                || lt instanceof LogicalType.JsonType
                || lt instanceof LogicalType.BsonType
                || lt instanceof LogicalType.IntervalType
                || lt instanceof LogicalType.NullType);
    }

    class IntBatchReader implements BatchReader {
        private final int[] data;
        private int pos;

        IntBatchReader(int height) {
            this.data = new int[height];
        }

        @Override
        public void append(ColumnReader reader) {
            int n = reader.getValueCount();
            System.arraycopy(reader.getInts(), 0, data, pos, n);
            pos += n;
        }

        @Override
        public Series<?> toSeries() {
            return Series.ofInt(data);
        }
    }

    class LongBatchReader implements BatchReader {
        private final long[] data;
        private int pos;

        LongBatchReader(int height) {
            this.data = new long[height];
        }

        @Override
        public void append(ColumnReader reader) {
            int n = reader.getValueCount();
            System.arraycopy(reader.getLongs(), 0, data, pos, n);
            pos += n;
        }

        @Override
        public Series<?> toSeries() {
            return Series.ofLong(data);
        }
    }

    class FloatBatchReader implements BatchReader {
        private final float[] data;
        private int pos;

        FloatBatchReader(int height) {
            this.data = new float[height];
        }

        @Override
        public void append(ColumnReader reader) {
            int n = reader.getValueCount();
            System.arraycopy(reader.getFloats(), 0, data, pos, n);
            pos += n;
        }

        @Override
        public Series<?> toSeries() {
            return Series.ofFloat(data);
        }
    }

    class DoubleBatchReader implements BatchReader {
        private final double[] data;
        private int pos;

        DoubleBatchReader(int height) {
            this.data = new double[height];
        }

        @Override
        public void append(ColumnReader reader) {
            int n = reader.getValueCount();
            System.arraycopy(reader.getDoubles(), 0, data, pos, n);
            pos += n;
        }

        @Override
        public Series<?> toSeries() {
            return Series.ofDouble(data);
        }
    }

    class BoolBatchReader implements BatchReader {
        private final boolean[] data;
        private int pos;

        BoolBatchReader(int height) {
            this.data = new boolean[height];
        }

        @Override
        public void append(ColumnReader reader) {
            int n = reader.getValueCount();
            System.arraycopy(reader.getBooleans(), 0, data, pos, n);
            pos += n;
        }

        @Override
        public Series<?> toSeries() {
            return Series.ofBool(data);
        }
    }

    class ObjectBatchReader implements BatchReader {

        private final BatchConverter converter;
        private final Object[] data;
        private final ValueCompactor<Object> compactor;
        private int pos;

        ObjectBatchReader(BatchConverter converter, int height, boolean compact) {
            this.converter = converter;
            this.data = new Object[height];
            this.compactor = compact ? new ValueCompactor<>() : null;
        }

        @Override
        public void append(ColumnReader reader) {
            int n = reader.getValueCount();
            converter.convert(reader, data, pos, n);

            if (compactor != null) {
                int end = pos + n;
                for (int i = pos; i < end; i++) {
                    data[i] = compactor.get(data[i]);
                }
            }

            pos += n;
        }

        @Override
        public Series<?> toSeries() {
            return Series.of(data);
        }
    }
}
