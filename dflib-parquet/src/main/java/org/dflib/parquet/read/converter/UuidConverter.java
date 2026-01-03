package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.Binary;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.nio.ByteBuffer;
import java.util.UUID;

class UuidConverter extends StoringPrimitiveConverter<UUID> {

    public static UuidConverter of(boolean accum, int accumCapacity, boolean dictionarySupport, boolean allowsNulls) {
        ValueStore<UUID> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new UuidConverter(store, dictionarySupport, allowsNulls);
    }

    private UUID[] dict;

    protected UuidConverter(ValueStore<UUID> store, boolean dictionarySupport, boolean allowsNulls) {
        super(store, dictionarySupport, allowsNulls);
    }

    @Override
    public void addBinary(Binary value) {
        store.push(convert(value));
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        dict = new UUID[maxId + 1];
        for (int i = 0; i <= maxId; i++) {
            dict[i] = convert(dictionary.decodeToBinary(i));
        }
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }

    private UUID convert(Binary value) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(value.getBytesUnsafe());
        long high = byteBuffer.getLong();
        long low = byteBuffer.getLong();
        return new UUID(high, low);
    }

}