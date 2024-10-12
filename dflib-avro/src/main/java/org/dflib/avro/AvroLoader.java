package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.avro.types.AvroTypeExtensions;
import org.dflib.builder.DataFrameAppender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AvroLoader {

    static {
        AvroTypeExtensions.initIfNeeded();
    }

    private Schema schema;
    private final List<ColConfigurator> colConfigurators;

    public AvroLoader() {
        this.colConfigurators = new ArrayList<>();
    }

    /**
     * Sets an explicit "reader" schema. If not set, embedded "writer" schema of the file will be used. Of course
     * the reader and the writer schema may differ, but should match each other per set of rules described in the
     * <a href="https://avro.apache.org/docs/current/spec.html#Schema+Resolution">Avro specification</a>.
     */
    public AvroLoader schema(Schema schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Configures an Avro column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     */
    public AvroLoader compactCol(int column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    /**
     * Configures an Avro column to be loaded with value compaction. Should be used to save memory for low-cardinality columns.
     */
    public AvroLoader compactCol(String column) {
        colConfigurators.add(ColConfigurator.objectCol(column, true));
        return this;
    }

    public DataFrame load(File file) {
        try (SeekableFileInput in = new SeekableFileInput(file)) {
            return load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro file: " + file, e);
        }
    }

    public DataFrame load(Path filePath) {
        return load(filePath.toFile());
    }

    public DataFrame load(String filePath) {
        return load(new File(filePath));
    }

    public DataFrame load(byte[] bytes) {
        try (SeekableByteArrayInput in = new SeekableByteArrayInput(bytes)) {
            return load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro bytes", e);
        }
    }

    protected DataFrame load(SeekableInput in) throws IOException {

        // Passing "reader" schema to GenericDatumReader. It is allowed to be null.
        // If not null, Avro will try to convert the file "writer" schema to the reader's expected format
        // See: https://avro.apache.org/docs/current/spec.html#Schema+Resolution

        GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
        DataFileReader<GenericRecord> inReader = new DataFileReader<>(in, reader);
        Schema schema = reader.getExpected();

        Index index = createIndex(schema);
        DataFrameAppender<GenericRecord> appender = DataFrame
                .byRow(extractors(index, schema))
                .columnIndex(index)
                .appender();

        // reuse "record" flyweight
        GenericRecord record = null;
        while (inReader.hasNext()) {
            record = inReader.next(record);
            appender.append(record);
        }

        return appender.toDataFrame();
    }

    protected Index createIndex(Schema schema) {
        // TODO: do we need to explicitly sort field by "order" to recreate save order?
        String[] labels = schema.getFields().stream().map(Schema.Field::name).toArray(String[]::new);
        return Index.of(labels);
    }

    protected Extractor<GenericRecord, ?>[] extractors(Index index, Schema schema) {

        // all non-null numeric and boolean columns can be used as boolean

        // TODO: do we need to explicitly sort field by "order" to recreate save order?

        Map<Integer, ColConfigurator> configurators = new HashMap<>();
        for (ColConfigurator c : colConfigurators) {
            // later configs override earlier configs at the same position
            configurators.put(c.srcPos(index), c);
        }

        int w = schema.getFields().size();
        Extractor<GenericRecord, ?>[] extractors = new Extractor[w];
        for (int i = 0; i < w; i++) {
            ColConfigurator  cc = configurators.computeIfAbsent(i, ii -> ColConfigurator.objectCol(ii, false));
            extractors[i] = cc.extractor(i, schema);
        }

        return extractors;
    }
}
