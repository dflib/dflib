package org.dflib.parquet;

/**
 * A subset of compression codecs in Parquet that are supported in DFLib without extra dependencies.
 */
public enum CompressionCodec {
    GZIP, LZ4_RAW, SNAPPY, ZSTD,
}
