package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.schema.PrimitiveType;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

import java.math.BigDecimal;
import java.math.BigInteger;

class DecimalConverter extends StoringPrimitiveConverter<BigDecimal> {


    public static DecimalConverter of(boolean accum, int accumCapacity, boolean allowNulls, PrimitiveType.PrimitiveTypeName type, int scale) {
        ValueStore<BigDecimal> store = accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>();
        return new DecimalConverter(store, allowNulls, type, scale);
    }

    private final PrimitiveType.PrimitiveTypeName type;
    private final int scale;
    private BigDecimal[] dict;

    protected DecimalConverter(ValueStore<BigDecimal> store, boolean allowsNulls, PrimitiveType.PrimitiveTypeName type, int scale) {
        super(store, allowsNulls);
        this.type = type;
        this.scale = scale;
    }

    @Override
    public void addBinary(Binary value) {
        store.push(convert(value));
    }

    @Override
    public void addInt(int value) {
        store.push(convert(value));
    }

    @Override
    public void addLong(long value) {
        store.push(convert(value));
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        store.push(dict[dictionaryId]);
    }

    @Override
    public boolean hasDictionarySupport() {
        return true;
    }

    @Override
    public void setDictionary(Dictionary dictionary) {
        int maxId = dictionary.getMaxId();
        dict = new BigDecimal[maxId + 1];
        switch (type) {
            case INT32:
                for (int i = 0; i <= maxId; i++) {
                    dict[i] = convert(dictionary.decodeToInt(i));
                }
                break;
            case INT64:
                for (int i = 0; i <= maxId; i++) {
                    dict[i] = convert(dictionary.decodeToLong(i));
                }
                break;
            case BINARY:
            case FIXED_LEN_BYTE_ARRAY:
                for (int i = 0; i <= maxId; i++) {
                    dict[i] = convert(dictionary.decodeToBinary(i));
                }
                break;
        }
    }

    private BigDecimal convert(Binary binary) {
        byte[] bytes = binary.getBytesUnsafe();
        return new BigDecimal(new BigInteger(bytes), scale);
    }

    private BigDecimal convert(long value) {
        return BigDecimal.valueOf(value, scale);
    }

}
