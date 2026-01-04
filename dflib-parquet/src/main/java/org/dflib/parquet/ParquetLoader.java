package org.dflib.parquet;

import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.io.api.GroupConverter;
import org.apache.parquet.schema.GroupType;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.Type;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.parquet.meta.MetaReader;
import org.dflib.parquet.read.BytesInputFile;
import org.dflib.parquet.read.DataFrameParquetReaderBuilder;
import org.dflib.parquet.read.SchemaProjector;
import org.dflib.parquet.read.converter.NoNullsRowConverter;
import org.dflib.parquet.read.converter.NullAwareRowConverter;
import org.dflib.parquet.read.converter.StoringConverter;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ParquetLoader {

    private SchemaProjector schemaProjector;
    private final List<ColConfigurator> colConfigurators;
    private boolean compactAllCols;

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
     * Configures all Parquet columns to be compacted during load. Should be used to save memory for low-cardinality
     * columns. Note that Parquet format is already compacted, so this operation doesn't incur much overhead, and in fact
     * can be faster than no compaction.
     *
     * @since 2.0.0
     */
    public ParquetLoader compactCols() {
        this.compactAllCols = true;
        this.colConfigurators.clear();
        return this;
    }

    /**
     * Configures Parquet columns to be compacted during load. Should be used to save memory for low-cardinality
     * columns. Note that Parquet format is already compacted, so this operation doesn't incur much overhead, and in fact
     * can be faster than no compaction.
     *
     * @since 2.0.0
     */
    public ParquetLoader compactCols(int... cols) {
        this.compactAllCols = false;
        for (int c : cols) {
            colConfigurators.add(ColConfigurator.objectCol(c, true));
        }
        return this;
    }

    /**
     * Configures Parquet columns to be compacted during load. Should be used to save memory for low-cardinality
     * columns. Note that Parquet format is already compacted, so this operation doesn't incur much overhead, and in fact
     * can be faster than no compaction.
     *
     * @since 2.0.0
     */
    public ParquetLoader compactCols(String... cols) {
        this.compactAllCols = false;
        for (String c : cols) {
            colConfigurators.add(ColConfigurator.objectCol(c, true));
        }
        return this;
    }

    /**
     * @deprecated in favor of {@link #compactCols(int...)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public ParquetLoader compactCol(int column) {
        return compactCols(column);
    }

    /**
     * @deprecated in favor of {@link #compactCols(String...)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public ParquetLoader compactCol(String column) {
        return compactCol(column);
    }

    public DataFrame load(File file) {
        return load(file.toPath());
    }

    public DataFrame load(String filePath) {
        return load(new File(filePath));
    }

    public DataFrame load(Path filePath) {
        return loadFromInputFile(new LocalInputFile(filePath), filePath::toString);
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
        return loadFromInputFile(new BytesInputFile(bytes), () -> resourceId);
    }

    private DataFrame loadFromInputFile(InputFile inputFile, Supplier<String> resourceId) {

        MetaReader.SchemaAndSize meta = MetaReader.schemaAndSize(inputFile, resourceId);
        MessageType projectedSchema = projectSchema(meta.schema());

        Index index = createIndex(projectedSchema);
        StoringConverter[] converters = converters(index, projectedSchema, meta.size());
        GroupConverter rowConverter = rowConverter(converters);

        try {
            ParquetReader<Object> reader = new DataFrameParquetReaderBuilder(inputFile, projectedSchema, rowConverter).build();
            do {
            } while (reader.read() != null);

        } catch (IOException e) {
            throw new UncheckedIOException("Error reading Parquet source: " + resourceId, e);
        }

        int w = index.size();
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = converters[i].accum().toSeries();
        }

        return new ColumnDataFrame(null, index, columns);
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

    private GroupConverter rowConverter(StoringConverter[] converters) {
        StoringConverter[] nullableConverters = Stream
                .of(converters)
                .filter(StoringConverter::allowsNulls)
                .toArray(StoringConverter[]::new);

        return nullableConverters.length > 0
                ? new NullAwareRowConverter(converters, nullableConverters)
                : new NoNullsRowConverter(converters);
    }

    private StoringConverter[] converters(Index index, GroupType schema, int capacity) {

        Map<Integer, ColConfigurator> configurators = new HashMap<>();

        // presumably "colConfigurators" will be empty if "compactAllCols == true", still doing a paranoid check
        if (!compactAllCols) {
            for (ColConfigurator c : colConfigurators) {
                // later configs override earlier configs at the same position
                configurators.put(c.srcPos(index), c);
            }
        }

        int w = schema.getFields().size();
        StoringConverter[] converters = new StoringConverter[w];
        for (int i = 0; i < w; i++) {
            converters[i] = configurators
                    .computeIfAbsent(i, ii -> ColConfigurator.objectCol(ii, compactAllCols))
                    .converter(schema.getFields().get(i), capacity);
        }

        return converters;
    }
}
