package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.avro.Avro;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseAvroSerializationTest {

    protected byte[] save(DataFrame df) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Avro.save(df, out);
        byte[] bytes = out.toByteArray();
        assertTrue(bytes.length > 0, "No bytes generated");
        return bytes;
    }

    protected DataFrame load(byte[] bytes) {
        DataFrame df = Avro.load(bytes);
        assertNotNull(df);
        return df;
    }

    protected DataFrame saveAndLoad(DataFrame df) {
        return load(save(df));
    }
}
