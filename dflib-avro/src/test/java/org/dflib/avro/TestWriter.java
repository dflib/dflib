package org.dflib.avro;

import org.apache.avro.Conversions;
import org.apache.avro.Schema;
import org.apache.avro.data.TimeConversions;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

class TestWriter<T> {

    public static <T> TestWriter<T> of(Class<T> type, Path baseFolder) {
        return new TestWriter<>(baseFolder.resolve(UUID.randomUUID() + ".avro"));
    }

    private final Path out;
    private Schema schema;
    private BiConsumer<GenericRecord, T> writer;

    private TestWriter(Path out) {
        this.out = out;
    }

    public TestWriter<T> writer(BiConsumer<GenericRecord, T> writer) {
        this.writer = writer;
        return this;
    }

    public TestWriter<T> schema(String schema) {
        this.schema = new Schema.Parser().parse(schema);
        return this;
    }

    public Path write(T... objects) {

        Objects.requireNonNull(schema);
        Objects.requireNonNull(writer);

        GenericData data = new GenericData();

        Stream.of(
                        new Conversions.DecimalConversion(),
                        new Conversions.BigDecimalConversion(),
                        new Conversions.UUIDConversion(),

                        new TimeConversions.DateConversion(),
                        new TimeConversions.TimeMicrosConversion(),
                        new TimeConversions.TimeMillisConversion(),
                        new TimeConversions.TimestampMicrosConversion(),
                        new TimeConversions.TimestampMillisConversion(),
                        new TimeConversions.TimestampNanosConversion(),
                        new TimeConversions.LocalTimestampMillisConversion(),
                        new TimeConversions.LocalTimestampMicrosConversion(),
                        new TimeConversions.LocalTimestampNanosConversion())

                .forEach(data::addLogicalTypeConversion);

        var datumWriter = new GenericDatumWriter<GenericRecord>(schema, data);

        try (var fileWriter = new DataFileWriter<>(datumWriter)) {
            fileWriter.create(schema, out.toFile());

            for (T o : objects) {
                GenericRecord r = new GenericData.Record(schema);
                writer.accept(r, o);
                fileWriter.append(r);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return out;
    }
}
