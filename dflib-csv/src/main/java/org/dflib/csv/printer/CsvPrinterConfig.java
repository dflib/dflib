package org.dflib.csv.printer;

import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvFormat;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Per-file configuration of the CSV writer.
 * <br>
 * Internal API. Part of the {@link org.dflib.csv.CsvSaver} API.
 *
 * @since 2.0.0
 */
public class CsvPrinterConfig {

    final CsvFormat csvFormat;
    final Charset encoding;
    final Codec compressionCodec;
    final boolean printHeader;

    CsvPrinterConfig(Builder builder) {
        this.csvFormat = builder.csvFormat;
        this.encoding = builder.encoding;
        this.compressionCodec = builder.compressionCodec;
        this.printHeader = builder.printHeader;
    }

    /**
     * Creates a new config from a builder with an overridden compression codec.
     */
    CsvPrinterConfig(Builder builder, Codec compressionCodec) {
        this.csvFormat = builder.csvFormat;
        this.encoding = builder.encoding;
        this.compressionCodec = compressionCodec;
        this.printHeader = builder.printHeader;
    }

    /**
     * Returns a new {@link Builder} seeded with default values.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns the configured CSV format.
     */
    public CsvFormat csvFormat() {
        return csvFormat;
    }

    /**
     * Returns the charset used when writing to byte-based sinks.
     */
    public Charset encoding() {
        return encoding;
    }

    /**
     * Returns the compression codec applied when writing to byte-based sinks, or {@code null} when no compression is
     * configured.
     */
    public Codec compressionCodec() {
        return compressionCodec;
    }

    /**
     * Returns whether the DataFrame index should be written as the first CSV row.
     */
    public boolean printHeader() {
        return printHeader;
    }

    /**
     * Fluent builder for {@link CsvPrinterConfig}.
     */
    public static class Builder {

        CsvFormat csvFormat;
        Charset encoding;
        Codec compressionCodec;
        boolean printHeader;

        Builder() {
            this.csvFormat = CsvFormat.defaultFormat().build();
            this.encoding = Charset.defaultCharset();
            this.compressionCodec = null;
            this.printHeader = true;
        }

        /**
         * Returns the currently configured compression codec, or {@code null} if none has been set.
         */
        public Codec compressionCodec() {
            return compressionCodec;
        }

        /**
         * Sets the CSV format used for writing. Must not be {@code null}.
         */
        public Builder csvFormat(CsvFormat csvFormat) {
            this.csvFormat = Objects.requireNonNull(csvFormat);
            return this;
        }

        /**
         * Sets the charset used when writing to byte-based sinks. Must not be {@code null}.
         */
        public Builder encoding(Charset encoding) {
            this.encoding = Objects.requireNonNull(encoding);
            return this;
        }

        /**
         * Sets the compression codec applied to byte-based sinks. Pass {@code null} to disable compression.
         */
        public Builder compressionCodec(Codec compressionCodec) {
            this.compressionCodec = compressionCodec;
            return this;
        }

        /**
         * Sets whether the DataFrame index is written as the first CSV row.
         */
        public Builder printHeader(boolean printHeader) {
            this.printHeader = printHeader;
            return this;
        }

        /**
         * Builds an immutable {@link CsvPrinterConfig} from the current builder state.
         */
        public CsvPrinterConfig build() {
            return new CsvPrinterConfig(this);
        }

        /**
         * Builds a config with the specified compression codec, overriding any previously set codec.
         * Does not mutate the builder state.
         */
        public CsvPrinterConfig buildWithCodec(Codec codec) {
            return new CsvPrinterConfig(this, codec);
        }
    }
}
