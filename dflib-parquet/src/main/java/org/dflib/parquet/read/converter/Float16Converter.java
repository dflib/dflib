package org.dflib.parquet.read.converter;

import org.apache.parquet.io.api.Binary;
import org.dflib.builder.FloatAccum;
import org.dflib.builder.FloatHolder;
import org.dflib.builder.ObjectAccum;
import org.dflib.builder.ObjectHolder;
import org.dflib.builder.ValueStore;

class Float16Converter extends StoringPrimitiveConverter<Float> {

    public static Float16Converter of(boolean accum, int accumCapacity, boolean allowsNulls) {
        ValueStore<Float> store = allowsNulls
                ? (accum ? new ObjectAccum<>(accumCapacity) : new ObjectHolder<>())
                : (accum ? new FloatAccum(accumCapacity) : new FloatHolder());

        return new Float16Converter(store, allowsNulls);
    }

    protected Float16Converter(ValueStore<Float> store, boolean allowsNulls) {
        super(store, allowsNulls);
    }

    @Override
    public void addBinary(Binary value) {
        store.pushFloat(convert(value));
    }

    private float convert(Binary value) {

        byte[] bytes = value.getBytesUnsafe();
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