package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadSchemaTest {

    @Test
    public void loadSchema() {

        String schemaJson = """
                {"type":"record","name":"x","namespace":"com.foo","fields":[{"name":"c","type":"bytes"}]}""";

        ByteArrayInputStream in = new ByteArrayInputStream(schemaJson.getBytes(StandardCharsets.UTF_8));
        Schema loaded = Avro.loadSchema(in);

        Schema ref = SchemaBuilder
                .record("x")
                .namespace("com.foo")
                .fields().name("c").type(Schema.create(Schema.Type.BYTES)).noDefault()
                .endRecord();

        assertEquals(ref, loaded);
    }
}
