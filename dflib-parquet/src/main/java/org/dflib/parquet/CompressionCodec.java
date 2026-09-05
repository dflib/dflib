package org.dflib.parquet;

/**
 * A subset of compression codecs in Parquet that DFLib supports on both read and write.
 */
public enum CompressionCodec {
    GZIP, LZ4_RAW, SNAPPY, ZSTD,
}
