package org.dflib.parquet;

/**
 * A subset of compression codecs in Parquet that are supported in DFLib without extra dependencies.
 *
 * @since 1.0.0-M23
 */
public enum CompressionCodec {
    GZIP, LZ4_RAW, SNAPPY, ZSTD,
}
