package org.dflib.parquet;

import org.apache.parquet.hadoop.ParquetFileWriter.Mode;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;
import org.apache.parquet.io.LocalOutputFile;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.parquet.write.ColumnMeta;
import org.dflib.parquet.write.DataFrameParquetWriterBuilder;
import org.dflib.parquet.write.DataFrameSchema;
import org.dflib.parquet.write.DecimalConfig;
import org.dflib.parquet.write.WriteConfiguration;
import org.dflib.row.RowProxy;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 1.0.0-M23
 */
public class ParquetSaver {

    private boolean createMissingDirs;
    private TimeUnit timeUnit = TimeUnit.MICROS;
    private CompressionCodecName compressionCodec = ParquetWriter.DEFAULT_COMPRESSION_CODEC_NAME;
    private DecimalConfig decimalConfig;

    public ParquetSaver createMissingDirs() {
        this.createMissingDirs = true;
        return this;
    }

    public ParquetSaver timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public ParquetSaver bigDecimal(int precision, int scale) {
        this.decimalConfig = new DecimalConfig(precision, scale);
        return this;
    }

    public ParquetSaver compressionCodec(CompressionCodecName compressionCodec) {
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
        try (ParquetWriter<RowProxy> parquetWriter = new DataFrameParquetWriterBuilder(new LocalOutputFile(filePath))
                .withWriteConfiguration(new WriteConfiguration(timeUnit, decimalConfig))
                .withDataFrameSchema(dataFrameSchema)
                .withCompressionCodec(compressionCodec)
                .withWriteMode(Mode.OVERWRITE)
                .build()) {
            for (RowProxy r : df) {
                parquetWriter.write(r);
            }
        }
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

}
