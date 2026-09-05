package org.dflib.hardwood;

/**
 * A subset of compression codecs in Parquet that DFLib supports on both read and write.
 *
 * @since 2.0.0
 */
public enum CompressionCodec {
    GZIP, LZ4_RAW, SNAPPY, ZSTD,
}
