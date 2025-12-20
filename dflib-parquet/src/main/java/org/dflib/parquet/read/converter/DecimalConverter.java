package org.dflib.parquet.read.converter;

import org.apache.parquet.column.Dictionary;
import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.PrimitiveConverter;
import org.apache.parquet.schema.PrimitiveType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

class DecimalConverter extends PrimitiveConverter {

    private final Consumer<Object> consumer;
    private final PrimitiveType.PrimitiveTypeName type;
    private final int scale;
    private BigDecimal[] dict;

    public DecimalConverter(Consumer<Object> consumer, PrimitiveType.PrimitiveTypeName type, int scale) {
        this.consumer = consumer;
        this.type = type;
        this.scale = scale;
    }

    @Override
    public void addBinary(Binary value) {
        consumer.accept(convert(value));
    }

    @Override
    public void addInt(int value) {
        consumer.accept(convert(value));
    }

    @Override
    public void addLong(long value) {
        consumer.accept(convert(value));
    }

    @Override
    public void addValueFromDictionary(int dictionaryId) {
        consumer.accept(dict[dictionaryId]);
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
        ByteBuffer value = ByteBuffer.wrap(binary.getBytes());
        // always copy the bytes out because BigInteger has no offset/length constructor
        byte[] bytes = new byte[value.remaining()];
        value.duplicate().get(bytes);
        return new BigDecimal(new BigInteger(bytes), scale);
    }

    private BigDecimal convert(long value) {
        return BigDecimal.valueOf(value, scale);
    }

}
