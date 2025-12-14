package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

class TestAvroWriter {

    public static <T> Builder<T> of(Class<T> type, Path baseFolder) {
        return new Builder<>(baseFolder.resolve(UUID.randomUUID() + ".avro"));
    }

    static final class Builder<T> {

        private final Path out;
        private Schema schema;
        private Consumer<GenericData> customizer;
        private BiConsumer<GenericRecord, T> writer;

        private Builder(Path out) {
            this.out = out;
        }

        public Builder<T> customizer(Consumer<GenericData> customizer) {
            this.customizer = customizer;
            return this;
        }

        public Builder<T> writer(BiConsumer<GenericRecord, T> writer) {
            this.writer = writer;
            return this;
        }

        public Builder<T> schema(String schema) {
            this.schema = new Schema.Parser().parse(schema);
            return this;
        }

        public Path write(T... objects) {

            Objects.requireNonNull(schema);
            Objects.requireNonNull(writer);

            GenericData data = new GenericData();
            if (customizer != null) {
                customizer.accept(data);
            }

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
}
