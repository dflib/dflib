package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Binary;
import org.apache.parquet.io.api.PrimitiveConverter;

import java.util.function.Consumer;

class Float16Converter extends PrimitiveConverter {

    private final Consumer<Object> consumer;

    public Float16Converter(Consumer<Object> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void addBinary(Binary value) {
        consumer.accept(convert(value));
    }

    private float convert(Binary value) {

        byte[] bytes = value.getBytes();
        short h = (short) ((bytes[1] << 8) | (bytes[0] & 0xFF));

        int sign = (h & 0x8000) << 16;
        int exp = (h >>> 10) & 0x1F;
        int mant = h & 0x03FF;

        int bits;

        if (exp == 0) {
            if (mant == 0) {
                bits = sign;
            } else {
                exp = 1;
                while ((mant & 0x0400) == 0) {
                    mant <<= 1;
                    exp--;
                }
                mant &= 0x03FF;
                bits = sign | ((exp + 127 - 15) << 23) | (mant << 13);
            }
        } else if (exp == 31) {
            bits = sign | 0x7F800000 | (mant << 13); // Inf / NaN
        } else {
            bits = sign | ((exp + 127 - 15) << 23) | (mant << 13);
        }

        return Float.intBitsToFloat(bits);
    }
}