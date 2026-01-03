package org.dflib.parquet;

import org.apache.parquet.io.LocalInputFile;
import org.apache.parquet.schema.MessageType;
import org.dflib.ByteSource;
import org.dflib.parquet.meta.MetaReader;
import org.dflib.parquet.read.BytesInputFile;

import java.io.File;
import java.nio.file.Path;

public class ParquetSchemaLoader {

    public MessageType load(Path filePath) {
        return MetaReader.schema(new LocalInputFile(filePath), filePath::toString);
    }

    public MessageType load(String filePath) {
        return load(new File(filePath));
    }

    public MessageType load(File file) {
        return load(file.toPath());
    }

    /**
     * @since 1.1.0
     */
    public MessageType load(ByteSource src) {
        return load(src.asBytes());
    }

    /**
     * @since 1.1.0
     */
    public MessageType load(byte[] bytes) {
        return MetaReader.schema(new BytesInputFile(bytes), () -> "?");
    }
}
