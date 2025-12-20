package org.dflib.parquet;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.conf.ParquetConfiguration;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.api.WriteSupport;
import org.apache.parquet.io.LocalOutputFile;
import org.apache.parquet.io.api.RecordConsumer;
import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;

class TestWriter<T> extends ParquetWriter.Builder<T, TestWriter<T>> {

    public static <T> TestWriter<T> of(Class<T> type, Path baseFolder) {
        return new TestWriter<>(type, baseFolder.resolve(UUID.randomUUID() + ".parquet"));
    }


    private final Class<T> type;
    private final Path out;
    private MessageType schema;
    private BiConsumer<RecordConsumer, T> writer;

    private TestWriter(Class<T> type, Path out) {
        super(new LocalOutputFile(out));
        this.type = type;
        this.out = out;
    }

    public TestWriter<T> writer(BiConsumer<RecordConsumer, T> writer) {
        this.writer = writer;
        return this;
    }

    public TestWriter<T> schema(String schema) {
        this.schema = MessageTypeParser.parseMessageType(schema);
        return this;
    }

    @Override
    protected TestWriter<T> self() {
        return this;
    }

    @Override
    protected WriteSupport<T> getWriteSupport(ParquetConfiguration conf) {
        return getWriteSupport();
    }

    @Override
    protected WriteSupport<T> getWriteSupport(Configuration conf) {
        return getWriteSupport();
    }

    private WriteSupport<T> getWriteSupport() {
        return new TestWriteSupport<>(
                type,
                Objects.requireNonNull(schema),
                Objects.requireNonNull(writer));
    }

    public Path write(T... objects) {
        try (ParquetWriter<T> writer = build()) {

            for (T o : objects) {
                writer.write(o);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return out;
    }


    static class TestWriteSupport<T> extends WriteSupport<T> {

        private final Class<T> type;
        private final MessageType schema;
        private final BiConsumer<RecordConsumer, T> writer;

        private RecordConsumer consumer;

        private TestWriteSupport(Class<T> type, MessageType schema, BiConsumer<RecordConsumer, T> writer) {
            this.type = type;
            this.schema = schema;
            this.writer = writer;
        }

        @Override
        public WriteContext init(Configuration conf) {
            Map<String, String> metadata = new HashMap<>();
            return new WriteContext(schema, metadata);
        }

        @Override
        public void prepareForWrite(RecordConsumer recordConsumer) {
            this.consumer = recordConsumer;
        }

        @Override
        public void write(T t) {
            writer.accept(consumer, t);
        }
    }
}
