package org.dflib.parquet;

import dev.hardwood.InputFile;
import dev.hardwood.metadata.ColumnChunk;
import dev.hardwood.metadata.ConvertedType;
import dev.hardwood.metadata.RowGroup;
import dev.hardwood.metadata.SchemaElement;
import dev.hardwood.reader.ColumnReader;
import dev.hardwood.reader.ColumnReaders;
import dev.hardwood.reader.ParquetFileReader;
import dev.hardwood.reader.RowReader;
import dev.hardwood.schema.ColumnProjection;
import dev.hardwood.schema.FileSchema;
import dev.hardwood.schema.SchemaNode;
import org.dflib.ByteSource;
import org.dflib.ByteSources;
import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.parquet.read.BatchReader;
import org.dflib.parquet.read.ColumnBuilder;
import org.dflib.parquet.read.SchemaProjector;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads .parquet files as DataFrames, using Hardwood as the Parquet engine.
 */
public class ParquetLoader {

    private final int minBatchHeight;

    private SchemaProjector schemaProjector;

    protected ParquetLoader(int minBatchHeight) {
        this.minBatchHeight = minBatchHeight;
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
    
    public DataFrame load(File file) {
        return load(file.toPath());
    }

    public DataFrame load(String filePath) {
        return load(new File(filePath));
    }

    public DataFrame load(Path filePath) {
        return loadFromInputFile(InputFile.of(filePath), filePath.toString());
    }

    /**
     * @since 1.1.0
     */
    public DataFrame load(ByteSource src) {
        return loadFromBytes(src.asBytes(), "?");
    }

    /**
     * @since 1.1.0
     */
    public Map<String, DataFrame> loadAll(ByteSources src) {
        return src.process((name, s) -> loadFromBytes(s.asBytes(), name));
    }

    private DataFrame loadFromBytes(byte[] bytes, String resourceId) {
        return loadFromInputFile(InputFile.of(ByteBuffer.wrap(bytes)), resourceId);
    }

    private DataFrame loadFromInputFile(InputFile inputFile, String resourceId) {
        // opened without a context of our own, so the reader creates one per read and shuts down its thread pool
        // when closed below
        try (ParquetFileReader reader = ParquetFileReader.open(inputFile)) {
            return load(reader, resourceId);
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading Parquet source: " + resourceId, e);
        }
    }

    private DataFrame load(ParquetFileReader reader, String resourceId) throws IOException {

        FileSchema schema = reader.getFileSchema();
        List<String> dfCols = projectColumns(schema);

        int w = dfCols.size();
        if (w == 0) {
            return DataFrame.empty();
        }

        int height = height(reader, resourceId);
        Map<String, ConvertedType> legacyTypes = legacyTypes(reader);
        Set<String> compactable = compactableCols(reader);

        Index index = Index.of(dfCols.toArray(new String[0]));
        Series<?>[] columns = batchReadable(schema, dfCols, height)
                ? readByColumn(reader, schema, dfCols, legacyTypes, compactable, height)
                : readByRow(reader, schema, dfCols, legacyTypes, compactable, height);

        return new ColumnDataFrame(null, index, columns);
    }

    /**
     * Whether to read through Hardwood's batch API, which decodes a column at a time and hands over whole arrays.
     * Nested columns can not be read that way, and send the entire file down the row-at-a-time path.
     */
    private boolean batchReadable(FileSchema schema, List<String> dfCols, int height) {

        if (height < minBatchHeight) {
            return false;
        }

        for (String c : dfCols) {
            if (!BatchReader.supports(schema.getField(c))) {
                return false;
            }
        }

        return true;
    }

    private Series<?>[] readByColumn(
            ParquetFileReader reader,
            FileSchema schema,
            List<String> dfCols,
            Map<String, ConvertedType> legacyTypes,
            Set<String> compactable,
            int height) {

        int w = dfCols.size();
        BatchReader[] readers = new BatchReader[w];
        for (int i = 0; i < w; i++) {
            String name = dfCols.get(i);
            readers[i] = BatchReader.of(schema.getField(name), legacyTypes.get(name), compactable.contains(name), height);
        }

        try (ColumnReaders columns = reader.columnReaders(projection(schema, dfCols))) {

            ColumnReader[] columnReaders = new ColumnReader[w];
            for (int i = 0; i < w; i++) {
                columnReaders[i] = columns.getColumnReader(dfCols.get(i));
            }

            while (columns.nextBatch()) {
                for (int i = 0; i < w; i++) {
                    readers[i].append(columnReaders[i]);
                }
            }
        }

        Series<?>[] series = new Series[w];
        for (int i = 0; i < w; i++) {
            series[i] = readers[i].toSeries();
        }

        return series;
    }

    private Series<?>[] readByRow(
            ParquetFileReader reader,
            FileSchema schema,
            List<String> dfCols,
            Map<String, ConvertedType> legacyTypes,
            Set<String> compactable,
            int height) {

        int w = dfCols.size();

        try (RowReader rows = rowReader(reader, schema, dfCols)) {

            // Hardwood presents projected columns in the file's own order, while DFLib must present them in the
            // order the caller asked for, so values are accumulated in Hardwood's order and placed in DFLib's
            ColumnBuilder[] builders = new ColumnBuilder[w];
            int[] positions = new int[w];

            for (int i = 0; i < w; i++) {
                String name = rows.getFieldName(i);
                builders[i] = ColumnBuilder.of(schema.getField(name), legacyTypes.get(name), compactable.contains(name), height);
                positions[i] = dfCols.indexOf(name);
            }

            while (rows.hasNext()) {
                rows.next();
                for (int i = 0; i < w; i++) {
                    builders[i].append(rows, i);
                }
            }

            Series<?>[] columns = new Series[w];
            for (int i = 0; i < w; i++) {
                columns[positions[i]] = builders[i].toSeries();
            }

            return columns;
        }
    }

    private List<String> projectColumns(FileSchema schema) {

        List<SchemaNode> fields = schema.getRootNode().children();
        List<String> all = new ArrayList<>(fields.size());
        for (SchemaNode f : fields) {
            all.add(f.name());
        }

        return schemaProjector != null ? schemaProjector.project(all) : all;
    }

    /**
     * Collects the legacy "converted type" of each schema element, keyed by name. The parsed schema only exposes a
     * column's modern logical type, but for a few annotations the legacy one is the more accurate of the two. See
     * {@link ColumnBuilder}.
     */
    private Map<String, ConvertedType> legacyTypes(ParquetFileReader reader) {

        Map<String, ConvertedType> types = new HashMap<>();
        for (SchemaElement e : reader.getFileMetaData().schema()) {
            if (e.convertedType() != null) {
                types.put(e.name(), e.convertedType());
            }
        }

        return types;
    }

    /**
     * Collects the names of the columns worth compacting on load, i.e. those the writer dictionary-encoded.
     *
     * <p>A dictionary page is the writer's own record that the column's values repeat: it lists the distinct values
     * once, and the data pages that follow reference them by index. A column written without one - a microsecond
     * timestamp, say - is one the writer found nothing to deduplicate in, and compacting it would build a cache with
     * an entry per row to discover the same thing. So the file's own encoding decides, and columns it left
     * undictionaried are read straight through.
     */
    private Set<String> compactableCols(ParquetFileReader reader) {

        Set<String> compactable = new HashSet<>();
        for (RowGroup rg : reader.getFileMetaData().rowGroups()) {
            for (ColumnChunk cc : rg.columns()) {
                if (cc.metaData().dictionaryPageOffset() != null) {
                    // a leaf of a nested column is named by its path; the DataFrame column is the root of that path
                    compactable.add(cc.metaData().pathInSchema().elements().get(0));
                }
            }
        }

        return compactable;
    }

    private int height(ParquetFileReader reader, String resourceId) {
        long height = reader.getFileMetaData().numRows();
        if (height > (long) Integer.MAX_VALUE) {
            throw new IllegalStateException("Parquet file is too large. Can read a max of "
                    + Integer.MAX_VALUE + " rows, actual size: " + height + ", source: " + resourceId);
        }

        return (int) height;
    }

    private RowReader rowReader(ParquetFileReader reader, FileSchema schema, List<String> dfCols) {
        return dfCols.size() == schema.getRootNode().children().size()
                ? reader.rowReader()
                : reader.buildRowReader().projection(projection(schema, dfCols)).build();
    }

    private ColumnProjection projection(FileSchema schema, List<String> dfCols) {
        // reading every column is not the same request as projecting all of them by name, and only the former lets
        // Hardwood skip the projection machinery
        return dfCols.size() == schema.getRootNode().children().size()
                ? ColumnProjection.all()
                : ColumnProjection.columns(dfCols.toArray(new String[0]));
    }
}
