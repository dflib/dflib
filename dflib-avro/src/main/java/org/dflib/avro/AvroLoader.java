package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.avro.schema.AvroSchemaUtils;
import org.dflib.avro.types.AvroTypeExtensions;
import org.dflib.builder.DataFrameAppender;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericDatumReader;
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
        DataFrameAppender<GenericRecord> appender = DataFrame
                .byRow(mapColumns(schema))
                .columnIndex(index)
                .appender();

        // reuse "record" flyweight
        GenericRecord record = null;
        while (inReader.hasNext()) {
            record = inReader.next(record);
            appender.append(record);
        }

        DataFrame df = appender.toDataFrame();
        return fromAvroTypes(df, schema);
    }

    protected Index createIndex(Schema schema) {
        // TODO: do we need to explicitly sort field by "order" to recreate save order?
        String[] labels = schema.getFields().stream().map(Schema.Field::name).toArray(String[]::new);
        return Index.of(labels);
    }

    protected Extractor<GenericRecord, ?>[] mapColumns(Schema schema) {

        // all non-null numeric and boolean columns can be used as boolean

        // TODO: do we need to explicitly sort field by "order" to recreate save order?

        List<Schema.Field> fields = schema.getFields();
        int w = fields.size();
        Extractor<GenericRecord, ?>[] extractors = new Extractor[w];
        for (int i = 0; i < w; i++) {
            extractors[i] = mapColumn(i, fields.get(i).schema());
        }

        return extractors;
    }

    protected Extractor<GenericRecord, ?> mapColumn(int pos, Schema columnSchema) {
        switch (columnSchema.getType()) {
            // Raw numeric and boolean types can be loaded as primitives,
            // as numeric nullable types are declared as unions and will fall under the "default" case
            case INT:
                return Extractor.$int(r -> (Integer) r.get(pos));
            case DOUBLE:
                return Extractor.$double(r -> (Double) r.get(pos));
            case LONG:
                return Extractor.$long(r -> (Long) r.get(pos));
            case BOOLEAN:
                return Extractor.$bool(r -> (Boolean) r.get(pos));
            case STRING:
            case BYTES:
            case ENUM:
            case NULL:
                return Extractor.$col(r -> r.get(pos));
            case UNION:
                return mapUnionColumn(pos, columnSchema.getTypes());
            default:
                throw new UnsupportedOperationException("(Yet) unsupported Avro schema type: " + columnSchema.getType());
        }
    }

    protected Extractor<GenericRecord, ?> mapUnionColumn(int pos, List<Schema> types) {
        // we only know how to handle union with NULL

        Schema[] otherThanNull = types.stream().filter(t -> t.getType() != Schema.Type.NULL).toArray(Schema[]::new);
        if (otherThanNull.length != 1) {
            throw new IllegalStateException("Can't handle union type that is not ['something', null]: " + types);
        }

        boolean hasNull = types.size() > 1;
        if (!hasNull) {
            // allow primitives
            return mapColumn(pos, otherThanNull[0]);
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
                return Extractor.$col(r -> r.get(pos));
            case UNION:
                return mapUnionColumn(pos, otherThanNull[0].getTypes());
            default:
                throw new UnsupportedOperationException("(Yet) unsupported Avro schema type: " + otherThanNull[0].getType());
        }
    }

    protected DataFrame fromAvroTypes(DataFrame df, Schema schema) {

        // GenericEnumSymbols are converted to enums if possible, or to Strings if not
        // (when the class is not known in the deserialization env)

        for (Schema.Field f : schema.getFields()) {
            Schema fSchema = f.schema().isUnion() ? AvroSchemaUtils.unpackUnion(f.schema()) : f.schema();

            if (AvroSchemaUtils.isEnum(fSchema)) {
                Class<Enum> enumType = AvroSchemaUtils.knownEnumType(fSchema);
                if (enumType != null) {
                    df = df.cols(f.name()).merge(Exp.$col(f.name()).castAsStr().castAsEnum(enumType));
                } else {
                    df = df.cols(f.name()).merge(Exp.$col(f.name()).castAsStr());
                }
            }
        }

        return df;
    }
}
