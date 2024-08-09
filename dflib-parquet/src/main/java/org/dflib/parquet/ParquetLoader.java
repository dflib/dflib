package org.dflib.parquet;

import org.apache.parquet.hadoop.ParquetReader;
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
import org.dflib.parquet.read.SchemaProjector;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

/**
 * @since 1.0.0-M23
 */
public class ParquetLoader {

    private SchemaProjector schemaProjector;

    /**
     * Configures the loader to only process the specified columns, and include them in the DataFrame in the specified
     * order.
     *
     * @return this loader instance
     */
    public ParquetLoader cols(String... columns) {
        this.schemaProjector = SchemaProjector.ofCols(columns);
        return this;
    }

    /**
     * @return this loader instance
     */
    public ParquetLoader cols(int... columns) {
        this.schemaProjector = SchemaProjector.ofCols(columns);
        return this;
    }

    /**
     * @return this loader instance
     */
    public ParquetLoader colsExcept(String... columns) {
        this.schemaProjector = SchemaProjector.ofColsExcept(columns);
        return this;
    }

    /**
     * @return this loader instance
     */
    public ParquetLoader colsExcept(int... columns) {
        this.schemaProjector = SchemaProjector.ofColsExcept(columns);
        return this;
    }

    public DataFrame load(File file) {
        return load(file.toPath());
    }

    public DataFrame load(String filePath) {
        return load(new File(filePath));
    }

    public DataFrame load(Path filePath) {
        try {

            // TODO: to avoid reading the schema twice, is it possible to defer schema extraction to
            //  DataFrameReadSupport.init(..) ?

            MessageType fileSchema = Parquet.schemaLoader().load(filePath);
            MessageType projectedSchema = projectSchema(fileSchema);

            DataFrameAppender<Object[]> appender = DataFrame.byArrayRow(mapColumns(projectedSchema))
                    .columnIndex(createIndex(projectedSchema))
                    .appender();

            LocalInputFile inputFile = new LocalInputFile(filePath);
            ParquetReader<Object[]> reader = new DataFrameParquetReaderBuilder(inputFile, projectedSchema).build();

            Object[] row;
            while ((row = reader.read()) != null) {
                appender.append(row);
            }
            return appender.toDataFrame();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private MessageType projectSchema(MessageType schema) {
        return schemaProjector != null
                ? schemaProjector.project(schema)
                : schema;
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
