package org.dflib.avro;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.dflib.ByteSource;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadSchemaTest {

    @Test
    public void loadFromJson_bytes() {

        String schemaJson = """
                {"type":"record","name":"x","namespace":"com.foo","fields":[\
                {"name":"c","type":"bytes"}]}""";

        Schema loaded = Avro.schemaLoader().loadFromJson(schemaJson.getBytes(StandardCharsets.UTF_8));

        Schema ref = SchemaBuilder
                .record("x")
                .namespace("com.foo")
                .fields().name("c").type(Schema.create(Schema.Type.BYTES)).noDefault()
                .endRecord();

        assertEquals(ref, loaded);
    }

    @Test
    public void loadFromAvro_ByteSource() {

        Schema loaded = Avro.schemaLoader().loadFromAvro(ByteSource.ofUrl(AvroLoaderTest.class.getResource("test1.avro")));

        String ref = """
                {"type":"record","name":"test1","namespace":"org.dflib.test","fields":[\
                {"name":"a","type":["int","null"]},\
                {"name":"b","type":["string","null"]}]}""";

        assertEquals(ref, loaded.toString());
    }

    @Test
    public void loadFromAvro_File() throws URISyntaxException {

        File avroFile = new File(AvroLoaderTest.class.getResource("test1.avro").toURI());
        Schema loaded = Avro.schemaLoader().loadFromAvro(avroFile);

        String ref = """
                {"type":"record","name":"test1","namespace":"org.dflib.test","fields":[\
                {"name":"a","type":["int","null"]},\
                {"name":"b","type":["string","null"]}]}""";

        assertEquals(ref, loaded.toString());
    }

    @Test
    public void loadFromAvro_Path() throws URISyntaxException {

        Path avroPath = Path.of(AvroLoaderTest.class.getResource("test1.avro").toURI());
        Schema loaded = Avro.schemaLoader().loadFromAvro(avroPath);

        String ref = """
                {"type":"record","name":"test1","namespace":"org.dflib.test","fields":[\
                {"name":"a","type":["int","null"]},\
                {"name":"b","type":["string","null"]}]}""";

        assertEquals(ref, loaded.toString());
    }
}
