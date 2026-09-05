package org.dflib.parquet.read;

import dev.hardwood.row.StructAccessor;
import org.dflib.Series;
import org.dflib.builder.BoolAccum;
import org.dflib.builder.DoubleAccum;
import org.dflib.builder.FloatAccum;
import org.dflib.builder.IntAccum;
import org.dflib.builder.LongAccum;

/**
 * {@link ColumnBuilder} implementations for "required" columns that map to a DFLib primitive Series, accumulating
 * values without boxing.
 *
 * @since 2.0.0
 */
public class PrimitiveColumnBuilders {

    @FunctionalInterface
    public interface IntReader {
        int read(StructAccessor row, int fieldIndex);
    }

    @FunctionalInterface
    public interface LongReader {
        long read(StructAccessor row, int fieldIndex);
    }

    @FunctionalInterface
    public interface FloatReader {
        float read(StructAccessor row, int fieldIndex);
    }

    public static ColumnBuilder ofInt(int capacity, IntReader reader) {
        IntAccum accum = new IntAccum(capacity);
        return new ColumnBuilder() {
            @Override
            public void append(StructAccessor row, int fieldIndex) {
                accum.pushInt(reader.read(row, fieldIndex));
            }

            @Override
            public Series<?> toSeries() {
                return accum.toSeries();
            }
        };
    }

    public static ColumnBuilder ofLong(int capacity, LongReader reader) {
        LongAccum accum = new LongAccum(capacity);
        return new ColumnBuilder() {
            @Override
            public void append(StructAccessor row, int fieldIndex) {
                accum.pushLong(reader.read(row, fieldIndex));
            }

            @Override
            public Series<?> toSeries() {
                return accum.toSeries();
            }
        };
    }

    public static ColumnBuilder ofFloat(int capacity, FloatReader reader) {
        FloatAccum accum = new FloatAccum(capacity);
        return new ColumnBuilder() {
            @Override
            public void append(StructAccessor row, int fieldIndex) {
                accum.pushFloat(reader.read(row, fieldIndex));
            }

            @Override
            public Series<?> toSeries() {
                return accum.toSeries();
            }
        };
    }

    public static ColumnBuilder ofDouble(int capacity) {
        DoubleAccum accum = new DoubleAccum(capacity);
        return new ColumnBuilder() {
            @Override
            public void append(StructAccessor row, int fieldIndex) {
                accum.pushDouble(row.getDouble(fieldIndex));
            }

            @Override
            public Series<?> toSeries() {
                return accum.toSeries();
            }
        };
    }

    public static ColumnBuilder ofBool(int capacity) {
        BoolAccum accum = new BoolAccum(capacity);
        return new ColumnBuilder() {
            @Override
            public void append(StructAccessor row, int fieldIndex) {
                accum.pushBool(row.getBoolean(fieldIndex));
            }

            @Override
            public Series<?> toSeries() {
                return accum.toSeries();
            }
        };
    }
}
