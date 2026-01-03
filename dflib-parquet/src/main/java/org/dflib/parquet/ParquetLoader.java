package org.dflib.parquet;

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
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.parquet.read.DataFrameParquetReaderBuilder;
import org.dflib.parquet.read.SchemaProjector;
import org.dflib.parquet.read.BytesInputFile;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParquetLoader {

    private SchemaProjector schemaProjector;
    private final List<ColConfigurator> colConfigurators;

    public ParquetLoader() {
        this.colConfigurators = new ArrayList<>();
    }

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

    /**
     * Configures a Parquet column to be loaded with value compaction. Should be used to save memory for low-cardinality
     * columns. Note that Parquet already does compaction on String columns by default, but some other column types
     * can take advantage of an explicit compaction.
     */
    public ParquetLoader compactCol(int column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures a Parquet column to be loaded with value compaction. Should be used to save memory for low-cardinality
     * columns. Note that Parquet already does compaction on String columns by default, but some other column types
     * can take advantage of an explicit compaction.
     */
    public ParquetLoader compactCol(String column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    public DataFrame load(File file) {
        return load(file.toPath());
    }

    public DataFrame load(String filePath) {
        return load(new File(filePath));
    }

    public DataFrame load(Path filePath) {
        return loadFromInputFile(new LocalInputFile(filePath), Parquet.schemaLoader().load(filePath), filePath.toString());
    }

    /**
     * @since 1.1.0
     */
    public DataFrame load(ByteSource src) {
        return src.processStream(st -> loadFromBytes(src.asBytes(), "?"));
    }

    /**
     * @since 1.1.0
     */
    public Map<String, DataFrame> loadAll(ByteSources src) {
        return src.process((name, s) -> loadFromBytes(s.asBytes(), name));
    }

    private DataFrame loadFromBytes(byte[] bytes, String resourceId) {
        return loadFromInputFile(new BytesInputFile(bytes), Parquet.schemaLoader().load(bytes), resourceId);
    }

    private DataFrame loadFromInputFile(InputFile inputFile, MessageType schema, String resourceId) {

        // TODO: to avoid reading the schema twice, is it possible to defer schema extraction to
        //  DataFrameReadSupport.init(..) ?

        MessageType projectedSchema = projectSchema(schema);

        Index index = createIndex(projectedSchema);

        DataFrameAppender<Object[]> appender = DataFrame.byArrayRow(extractors(index, projectedSchema))
                .columnIndex(index)
                .appender();

        try {
            ParquetReader<Object[]> reader = new DataFrameParquetReaderBuilder(inputFile, projectedSchema).build();

            Object[] row;
            while ((row = reader.read()) != null) {
                appender.append(row);
            }

        } catch (IOException e) {
            throw new UncheckedIOException("Error reading source: " + resourceId, e);
        }

        return appender.toDataFrame();
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

    private Extractor<Object[], ?>[] extractors(Index index, GroupType schema) {

        Map<Integer, ColConfigurator> configurators = new HashMap<>();
        for (ColConfigurator c : colConfigurators) {
            // later configs override earlier configs at the same position
            configurators.put(c.srcPos(index), c);
        }

        int w = schema.getFields().size();
        Extractor<Object[], ?>[] extractors = new Extractor[w];
        for (int i = 0; i < w; i++) {
            ColConfigurator cc = configurators.computeIfAbsent(i, ii -> ColConfigurator.objectCol(ii, false));
            extractors[i] = cc.extractor(i, schema);
        }

        return extractors;
    }

}
