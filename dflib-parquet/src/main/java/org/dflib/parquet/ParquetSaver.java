package org.dflib.parquet;

import dev.hardwood.OutputFile;
import dev.hardwood.schema.FileSchema;
import dev.hardwood.writer.ColumnWriter;
import dev.hardwood.writer.ParquetFileWriter;
import dev.hardwood.writer.PrecisionLossPolicy;
import dev.hardwood.writer.WriterConfig;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.parquet.write.BatchFieldWriter;
import org.dflib.parquet.write.ColumnMeta;
import org.dflib.parquet.write.DataFrameSchema;
import org.dflib.parquet.write.DecimalConfig;
import org.dflib.parquet.write.SchemaCompiler;
import org.dflib.parquet.write.WriteConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves DataFrames as .parquet files, using Hardwood as the Parquet engine.
 */
public class ParquetSaver {

    /**
     * How many rows go into one column batch. Bounds the memory a batch holds - a batch of a binary column
     * materializes a byte array per value - without giving up the batch API's advantage.
     */
    private static final int BATCH_ROWS = 1_000_000;

    private boolean createMissingDirs;
    private TimeUnit timeUnit = TimeUnit.MICROS;
    private CompressionCodec compressionCodec;
    private DecimalConfig decimalConfig;

    public ParquetSaver createMissingDirs() {
        this.createMissingDirs = true;
        return this;
    }

    public ParquetSaver timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    /**
     * @deprecated in favor of {@link #decimalSize(int, int)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public ParquetSaver bigDecimal(int precision, int scale) {
        return decimalSize(precision, scale);
    }

    /**
     * @since 2.0.0
     */
    public ParquetSaver decimalSize(int precision, int scale) {
        this.decimalConfig = new DecimalConfig(precision, scale);
        return this;
    }

    public ParquetSaver compression(CompressionCodec compressionCodec) {
        this.compressionCodec = compressionCodec;
        return this;
    }

    public void save(DataFrame df, File file) {
        save(df, file.toPath());
    }

    public void save(DataFrame df, String fileName) {
        save(df, new File(fileName));
    }

    public void save(DataFrame df, Path filePath) {
        createMissingDirsIfNeeded(filePath);
        try {
            doSave(df, filePath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void doSave(DataFrame df, Path filePath) throws IOException {

        DataFrameSchema dataFrameSchema = extractDataFrameSchema(df);
        FileSchema schema = new SchemaCompiler(writeConfiguration()).compileSchema(dataFrameSchema);

        try (ParquetFileWriter writer = ParquetFileWriter.create(OutputFile.of(filePath), schema, writerConfig())) {
            writeByColumn(df, dataFrameSchema, writer);
        }
    }

    private void writeByColumn(DataFrame df, DataFrameSchema dataFrameSchema, ParquetFileWriter writer)
            throws IOException {

        List<ColumnMeta> columns = dataFrameSchema.getColumns();
        int w = columns.size();

        BatchFieldWriter[] fieldWriters = new BatchFieldWriter[w];
        for (int i = 0; i < w; i++) {
            ColumnMeta column = columns.get(i);
            fieldWriters[i] = BatchFieldWriter.of(column, df.getColumn(column.getColumnName()), writeConfiguration());
        }

        int height = df.height();
        ColumnWriter batches = writer.columnWriter();

        for (int start = 0; start < height; start += BATCH_ROWS) {
            int len = Math.min(BATCH_ROWS, height - start);
            int from = start;
            batches.writeBatch(batch -> {
                for (BatchFieldWriter fw : fieldWriters) {
                    fw.write(batch, from, len);
                }
            });
        }
    }

    private WriterConfig writerConfig() {
        return WriterConfig.builder()
                .codec(compressionCodecName())

                // DFLib truncates times and timestamps that are finer than the column's unit, rather than failing on
                // them. Decimals are separately validated and rescaled before they reach Hardwood, so this policy
                // never causes a decimal to be silently rounded. See "BigDecimalWriter".
                .precisionLossPolicy(PrecisionLossPolicy.TRUNCATE)
                .build();
    }

    private WriteConfiguration writeConfiguration() {
        return new WriteConfiguration(timeUnit, decimalConfig);
    }

    private void createMissingDirsIfNeeded(Path filePath) {
        if (createMissingDirs) {
            File dir = filePath.toFile().getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
        }
    }

    private DataFrameSchema extractDataFrameSchema(DataFrame df) {
        List<ColumnMeta> result = new ArrayList<>();
        int index = 0;
        for (String column : df.getColumnsIndex()) {
            Series<Object> series = df.getColumn(column);
            Class<?> inferredType = series.getInferredType();
            result.add(new ColumnMeta(column, inferredType, index));
            index++;
        }
        return new DataFrameSchema(result);
    }

    private dev.hardwood.metadata.CompressionCodec compressionCodecName() {
        if (compressionCodec == null) {
            return dev.hardwood.metadata.CompressionCodec.UNCOMPRESSED;
        }

        return switch (compressionCodec) {
            case GZIP -> dev.hardwood.metadata.CompressionCodec.GZIP;
            case ZSTD -> dev.hardwood.metadata.CompressionCodec.ZSTD;
            case SNAPPY -> dev.hardwood.metadata.CompressionCodec.SNAPPY;
            case LZ4_RAW -> dev.hardwood.metadata.CompressionCodec.LZ4_RAW;
        };
    }
}
