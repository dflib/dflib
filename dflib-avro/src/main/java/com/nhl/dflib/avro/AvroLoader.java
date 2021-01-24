package com.nhl.dflib.avro;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DataFrameByRowBuilder;
import com.nhl.dflib.Index;
import com.nhl.dflib.accumulator.*;
import com.nhl.dflib.avro.schema.AvroSchemaUtils;
import com.nhl.dflib.avro.types.AvroTypeExtensions;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericRecord;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class AvroLoader {

    static {
        AvroTypeExtensions.initIfNeeded();
    }

    private Schema schema;

    /**
     * Sets an explicit "reader" schema. If not set, embedded "writer" schema of the file will be used. Of course
     * the reader and the writer schema may differ, but should match each other per set of rules described in the
     * <a href="https://avro.apache.org/docs/current/spec.html#Schema+Resolution">Avro specification</a>.
     */
    public AvroLoader schema(Schema schema) {
        this.schema = schema;
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
        DataFrameByRowBuilder dfb = DataFrame.newFrame(index).byRow(mapColumns(schema));

        // reusing both Avro record and rowHolder flyweights..
        GenericRecord record = null;
        Object[] rowHolder = new Object[index.size()];

        while (inReader.hasNext()) {
            record = inReader.next(record);
            dfb.addRow(recordToRow(record, rowHolder));
        }

        DataFrame df = dfb.create();
        return fromAvroTypes(df, schema);
    }

    protected Object[] recordToRow(GenericRecord record, Object[] rowHolder) {

        for (int i = 0; i < rowHolder.length; i++) {
            rowHolder[i] = record.get(i);
        }

        return rowHolder;
    }

    protected Index createIndex(Schema schema) {
        // TODO: do we need to explicitly sort field by "order" to recreate save order?
        String[] labels = schema.getFields().stream().map(Schema.Field::name).toArray(String[]::new);
        return Index.forLabels(labels);
    }

    protected Accumulator<?>[] mapColumns(Schema schema) {

        // all non-null numeric and boolean columns can be used as boolean

        // TODO: do we need to explicitly sort field by "order" to recreate save order?
        return schema.getFields().stream().map(Schema.Field::schema).map(this::mapColumn).toArray(Accumulator[]::new);
    }

    protected Accumulator<?> mapColumn(Schema columnSchema) {
        switch (columnSchema.getType()) {
            // Raw numeric and boolean types can be loaded as primitives,
            // as numeric nullable types are declared as unions and will fall under the "default" case
            case INT:
                return new IntAccumulator();
            case DOUBLE:
                return new DoubleAccumulator();
            case LONG:
                return new LongAccumulator();
            case BOOLEAN:
                return new BooleanAccumulator();
            case STRING:
            case BYTES:
            case ENUM:
                return new ObjectAccumulator<>();
            case UNION:
                return mapUnionColumn(columnSchema.getTypes());
            default:
                throw new UnsupportedOperationException("(Yet) unsupported Avro schema type: " + columnSchema.getType());
        }
    }

    protected Accumulator<?> mapUnionColumn(List<Schema> types) {
        // we only know how to handle union with NULL

        Schema[] otherThanNull = types.stream().filter(t -> t.getType() != Schema.Type.NULL).toArray(Schema[]::new);
        if (otherThanNull.length != 1) {
            throw new IllegalStateException("Can't handle union type that is not ['something', null]: " + types);
        }

        boolean hasNull = types.size() > 1;
        if (!hasNull) {
            // allow primitives
            return mapColumn(otherThanNull[0]);
        }

        // don't allow primitives
        switch (otherThanNull[0].getType()) {
            case INT:
            case DOUBLE:
            case LONG:
            case BOOLEAN:
            case STRING:
            case BYTES:
            case ENUM:
                return new ObjectAccumulator<>();
            case UNION:
                return mapUnionColumn(otherThanNull[0].getTypes());
            default:
                throw new UnsupportedOperationException("(Yet) unsupported Avro schema type: " + otherThanNull[0].getType());
        }
    }

    protected DataFrame fromAvroTypes(DataFrame df, Schema schema) {

        // 1. GenericEnumSymbols must be converted to enums

        for (Schema.Field f : schema.getFields()) {
            Schema fSchema = f.schema().isUnion() ? AvroSchemaUtils.unpackUnion(f.schema()) : f.schema();

            if (AvroSchemaUtils.isEnum(fSchema)) {
                df = df.convertColumn(f.name(), (GenericEnumSymbol<?> v) -> AvroSchemaUtils.toEnum(v));
            }
        }

        return df;
    }
}
