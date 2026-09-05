package org.dflib.parquet.read;

import dev.hardwood.row.StructAccessor;
import org.dflib.Series;
import org.dflib.builder.ObjectAccum;

/**
 * A {@link ColumnBuilder} accumulating boxed values.
 *
 * <p>When the file dictionary-encoded the column, the builder decodes each distinct value once and hands the same
 * instance to every row repeating it, keyed on the raw physical value it was decoded from. Reading that key is
 * cheaper than decoding - and on a hit the decoder is not called at all, which on this path is where the saving is:
 * Hardwood's row accessors decode from scratch on every call.
 *
 * @since 2.0.0
 */
public class ObjectColumnBuilder<T> implements ColumnBuilder {

    protected final ValueReader<T> reader;
    protected final ObjectAccum<T> accum;

    public static <T> ObjectColumnBuilder<T> of(int capacity, ValueReader<T> reader) {
        return new ObjectColumnBuilder<>(reader, capacity);
    }

    /**
     * Creates a builder for a column of a fixed-width physical type, keying its dictionary on that physical value.
     *
     * @param dictionary whether to decode through a dictionary at all, i.e. whether the file dictionary-encoded the
     *                   column
     * @param key        reads the undecoded physical value the dictionary is keyed on
     */
    public static <T> ObjectColumnBuilder<T> of(int capacity, boolean dictionary, KeyReader key, ValueReader<T> reader) {
        return dictionary
                ? new KeyedBuilder<>(reader, key, capacity)
                : new ObjectColumnBuilder<>(reader, capacity);
    }

    /**
     * Creates a builder for a byte-array-backed column, keying its dictionary on the value's bytes. Reading those
     * costs an array copy, but it is the same copy Hardwood's own decoders make on the way to the decoded value, so
     * a dictionary hit still skips everything past it.
     */
    public static <T> ObjectColumnBuilder<T> ofBinaryKeyed(int capacity, boolean dictionary, ValueReader<T> reader) {
        return dictionary
                ? new BinaryKeyedBuilder<>(reader, capacity)
                : new ObjectColumnBuilder<>(reader, capacity);
    }

    protected ObjectColumnBuilder(ValueReader<T> reader, int capacity) {
        this.reader = reader;
        this.accum = new ObjectAccum<>(capacity);
    }

    @Override
    public void append(StructAccessor row, int fieldIndex) {
        accum.push(reader.read(row, fieldIndex));
    }

    @Override
    public Series<?> toSeries() {
        return accum.toSeries();
    }

    static class KeyedBuilder<T> extends ObjectColumnBuilder<T> {

        private final KeyReader key;
        private final LongKeyDictionary dictionary;

        KeyedBuilder(ValueReader<T> reader, KeyReader key, int capacity) {
            super(reader, capacity);
            this.key = key;
            this.dictionary = new LongKeyDictionary();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void append(StructAccessor row, int fieldIndex) {

            // the physical accessors throw on a null, so nulls are recognized before the key is read
            if (row.isNull(fieldIndex)) {
                accum.push(null);
                return;
            }

            long k = key.read(row, fieldIndex);

            Object shared = dictionary.get(k);
            if (shared != null) {
                accum.push((T) shared);
                return;
            }

            T decoded = reader.read(row, fieldIndex);
            dictionary.put(k, decoded);
            accum.push(decoded);
        }
    }

    static class BinaryKeyedBuilder<T> extends ObjectColumnBuilder<T> {

        private final BytesKeyDictionary dictionary;

        BinaryKeyedBuilder(ValueReader<T> reader, int capacity) {
            super(reader, capacity);
            this.dictionary = new BytesKeyDictionary();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void append(StructAccessor row, int fieldIndex) {

            byte[] k = row.getBinary(fieldIndex);
            if (k == null) {
                accum.push(null);
                return;
            }

            Object shared = dictionary.get(k, 0, k.length);
            if (shared != null) {
                accum.push((T) shared);
                return;
            }

            T decoded = reader.read(row, fieldIndex);
            dictionary.put(k, 0, k.length, decoded);
            accum.push(decoded);
        }
    }
}
