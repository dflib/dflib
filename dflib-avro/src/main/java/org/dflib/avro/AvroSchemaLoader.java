package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.file.SeekableByteArrayInput;
import org.apache.avro.file.SeekableFileInput;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericDatumReader;
import org.dflib.ByteSource;
import org.dflib.avro.types.AvroTypeExtensions;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Supplier;


public class AvroSchemaLoader {

    static {
        AvroTypeExtensions.initIfNeeded();
    }

    /**
     * @deprecated in favor of {@link #loadFromJson(Path)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Schema load(Path filePath) {
        return loadFromJson(filePath);
    }

    /**
     * @deprecated in favor of {@link #loadFromJson(String)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Schema load(String filePath) {
        return loadFromJson(filePath);
    }

    /**
     * @deprecated in favor of {@link #loadFromJson(File)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Schema load(File file) {
        return loadFromJson(file);
    }

    /**
     * @deprecated in favor of {@link #loadFromJson(ByteSource)}
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public Schema load(InputStream in) {
        return loadFromJsonStream(in, () -> "?");
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromJson(String filePath) {
        return loadFromJson(new File(filePath));
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromJson(Path filePath) {
        return loadFromJson(filePath.toFile());
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromJson(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            return new Schema.Parser().parse(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro schema JSON: " + file, e);
        }
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromJson(byte[] src) {
        return loadFromJsonStream(new ByteArrayInputStream(src), () -> "?");
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromJson(ByteSource src) {
        return src.processStream(in -> loadFromJsonStream(in, () -> "?"));
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromAvro(String filePath) {
        return loadFromAvro(new File(filePath));
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromAvro(Path path) {
        return loadFromAvro(path.toFile());
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromAvro(File file) {
        try (SeekableFileInput in = new SeekableFileInput(file)) {
            return loadFromAvroSeekable(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro schema: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromAvro(byte[] src) {
        try (SeekableByteArrayInput in = new SeekableByteArrayInput(src)) {
            return loadFromAvroSeekable(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro schema", e);
        }
    }

    /**
     * @since 2.0.0
     */
    public Schema loadFromAvro(ByteSource src) {
        return src.processStream(in -> loadFromAvroStream(in, () -> "?"));
    }

    private Schema loadFromJsonStream(InputStream in, Supplier<String> resourceId) {
        try {
            return new Schema.Parser().parse(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro schema from JSON: " + resourceId.get(), e);
        }
    }

    private Schema loadFromAvroStream(InputStream in, Supplier<String> resourceId) {
        try (DataFileStream<?> dfs = new DataFileStream<>(in, new GenericDatumReader<>())) {
            return dfs.getSchema();
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro schema: " + resourceId.get(), e);
        }
    }

    private Schema loadFromAvroSeekable(SeekableInput in) throws IOException {
        try (DataFileReader<?> dfr = new DataFileReader<>(in, new GenericDatumReader<>())) {
            return dfr.getSchema();
        }
    }
}
