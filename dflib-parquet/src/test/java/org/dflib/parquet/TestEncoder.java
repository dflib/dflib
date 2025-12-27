package org.dflib.parquet;

import org.apache.parquet.io.api.Binary;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.UUID;

class TestEncoder {

    public static Binary floatToBytes(float f) {
        byte[] out = ByteBuffer
                .allocate(2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(floatToFloat16(f))
                .array();
        return Binary.fromConstantByteArray(out);
    }

    public static Binary intsToBytes(int[] ints) {
        int w = ints.length;
        ByteBuffer bb = ByteBuffer
                .allocate(w * Integer.BYTES)

                // that's how "INTERVAL" type works
                .order(ByteOrder.LITTLE_ENDIAN);

        for (int i = 0; i < w; i++) {
            bb.putInt(ints[i]);
        }

        return Binary.fromConstantByteArray(bb.array());
    }

    public static short floatToFloat16(float f) {
        int bits = Float.floatToIntBits(f);

        int sign = (bits >>> 16) & 0x8000;
        int exp = (bits >>> 23) & 0xFF;
        int mant = bits & 0x7FFFFF;

        if (exp == 0xFF) {
            if (mant == 0) {
                return (short) (sign | 0x7C00);
            }
            return (short) (sign | 0x7E00);
        }

        int halfExp = exp - 127 + 15;

        if (halfExp <= 0) {
            if (halfExp < -10) {
                return (short) sign;
            }
            mant = (mant | 0x800000) >> (1 - halfExp);
            return (short) (sign | (mant >> 13));
        }

        if (halfExp >= 31) {
            return (short) (sign | 0x7C00);
        }

        return (short) (sign | (halfExp << 10) | (mant >> 13));
    }


    public static Binary decimalToBytes(BigDecimal v, int scale, int numBytes) {
        BigInteger unscaled = v.setScale(scale).unscaledValue();
        byte[] raw = unscaled.toByteArray();

        byte sign = (byte) (unscaled.signum() < 0 ? 0xFF : 0x00);
        byte[] out = new byte[numBytes];

        int srcPos = Math.max(0, raw.length - numBytes);
        int dstPos = Math.max(0, numBytes - raw.length);
        int len = Math.min(raw.length, numBytes);

        Arrays.fill(out, 0, dstPos, sign);
        System.arraycopy(raw, srcPos, out, dstPos, len);
        return Binary.fromConstantByteArray(out);
    }

    public static int decimalToInt(BigDecimal v, int scale) {
        return v.setScale(scale).unscaledValue().intValueExact();
    }

    public static long decimalToLong(BigDecimal v, int scale) {
        return v.setScale(scale).unscaledValue().longValueExact();
    }

    public static Binary uuidToBytes(UUID uuid) {
        long hi = uuid.getMostSignificantBits();
        long lo = uuid.getLeastSignificantBits();

        byte[] out = new byte[16];
        appendInt((int) (hi >> 32), out, 0);
        appendInt((int) hi, out, 4);
        appendInt((int) (lo >> 32), out, 8);
        appendInt((int) lo, out, 12);
        return Binary.fromConstantByteArray(out);
    }

    private static void appendInt(int value, byte[] bytes, int offset) {
        bytes[offset] = (byte) (value >> 24);
        bytes[++offset] = (byte) (value >> 16);
        bytes[++offset] = (byte) (value >> 8);
        bytes[++offset] = (byte) value;
    }
}
