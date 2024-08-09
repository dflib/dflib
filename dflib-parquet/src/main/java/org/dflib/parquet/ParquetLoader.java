package org.dflib.parquet;

import org.apache.parquet.ParquetReadOptions;
import org.apache.parquet.conf.PlainParquetConfiguration;
import org.apache.parquet.hadoop.ParquetFileReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.builder.DataFrameAppender;
import org.dflib.parquet.read.DataFrameParquetReaderBuilder;
import org.dflib.parquet.read.RowExtractorFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @since 1.0.0-M23
 */
public class ParquetLoader {

    public DataFrame load(File file) {
        return load(file.toPath());
    }

    public DataFrame load(String filePath) {
        return load(new File(filePath));
    }

    public DataFrame load(Path filePath) {
        try {
            LocalInputFile inputFile = new LocalInputFile(filePath);
            MessageType schema = loadSchema(inputFile);

            DataFrameAppender<Object[]> appender = DataFrame.byArrayRow(mapColumns(schema))
                    .columnIndex(createIndex(schema))
                    .appender();

            ParquetReader<Object[]> reader = new DataFrameParquetReaderBuilder(inputFile).build();

            Object[] row;
            while ((row = reader.read()) != null) {
                appender.append(row);
            }
            return appender.toDataFrame();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private MessageType loadSchema(InputFile inputFile) throws IOException {
        ParquetReadOptions parquetReadOptions = ParquetReadOptions.builder(new PlainParquetConfiguration()).build();
        try (ParquetFileReader parquetFile = new ParquetFileReader(inputFile, parquetReadOptions)) {
            return parquetFile.getFileMetaData().getSchema();
        }
    }

    private Index createIndex(GroupType schema) {
        String[] labels = schema.getFields().stream().map(Type::getName).toArray(String[]::new);
        return Index.of(labels);
    }

    private Extractor<Object[], ?>[] mapColumns(GroupType schema) {
        List<Type> fields = schema.getFields();
        Extractor<Object[], ?>[] extractors = new Extractor[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            extractors[i] = RowExtractorFactory.converterFor(fields.get(i), i);
        }
        return extractors;
    }

}
