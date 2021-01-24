package com.nhl.dflib.avro;

import com.nhl.dflib.avro.types.AvroTypeExtensions;
import org.apache.avro.Schema;
import org.apache.avro.file.SeekableFileInput;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @since 0.11
 */
public class AvroSchemaLoader {

    static {
        AvroTypeExtensions.initIfNeeded();
    }

    public Schema load(Path filePath) {
        return load(filePath.toFile());
    }

    public Schema load(String filePath) {
        return load(new File(filePath));
    }

    public Schema load(File file) {
        try (SeekableFileInput in = new SeekableFileInput(file)) {
            return load(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro schema file: " + file, e);
        }
    }

    public Schema load(InputStream in) {
        try {
            return new Schema.Parser().parse(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Avro bytes", e);
        }
    }
}
