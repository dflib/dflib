package org.dflib.hardwood.read;

import dev.hardwood.metadata.ConvertedType;
import dev.hardwood.metadata.LogicalType;

import java.math.BigInteger;

/**
 * Shared decoding rules for Parquet logical types, used by both the row and the batch read paths.
 */
class LogicalTypes {

    /**
     * Reconciles a column's modern logical type with its legacy converted type.
     *
     * <p>The Parquet logical type union has no INTERVAL member - field 9 is reserved but was never defined - so
     * "parquet-java" annotates an INTERVAL column as UNKNOWN in the union while keeping INTERVAL as the legacy
     * converted type. Hardwood defined an IntervalType of its own on the reserved field, and lets the union win over
     * the converted type, so it reads such a column as a NULL-typed one, silently turning every value into a null.
     * Deferring to the legacy annotation when the union says UNKNOWN recovers the intended type.
     *
     * <p>Files written by Hardwood itself are unaffected: they carry no union member for INTERVAL at all, and so
     * already resolve through the converted type.
     */
    static LogicalType effective(LogicalType logicalType, ConvertedType legacyType) {
        return logicalType instanceof LogicalType.NullType && legacyType == ConvertedType.INTERVAL
                ? new LogicalType.IntervalType()
                : logicalType;
    }

    /**
     * Reads a 64-bit unsigned int, which has no Java primitive counterpart.
     */
    static BigInteger toUnsignedBigInteger(long v) {
        return v >= 0 ? BigInteger.valueOf(v) : new BigInteger(Long.toUnsignedString(v));
    }
}
