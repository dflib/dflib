package org.dflib.json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.row.RowProxy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Path;


public class JsonSaver {

    private boolean createMissingDirs;

    /**
     * Instructs the saver to create any missing directories in the file path.
     *
     * @return this saver instance
     */
    public JsonSaver createMissingDirs() {
        this.createMissingDirs = true;
        return this;
    }

    public void save(DataFrame df, Appendable out) {

        try {
            doSave(df, AppendableWriter.asWriter(out));
        } catch (IOException e) {
            throw new RuntimeException("Error writing records as Avro: " + e.getMessage(), e);
        }
    }

    public void save(DataFrame df, File file) {

        createMissingDirsIfNeeded(file);
        try (FileWriter out = new FileWriter(file)) {
            save(df, out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing Avro file '" + file + "': " + e.getMessage(), e);
        }
    }

    public void save(DataFrame df, Path filePath) {
        save(df, filePath.toFile());
    }

    public void save(DataFrame df, String fileName) {
        save(df, new File(fileName));
    }

    
    public String saveToString(DataFrame df) {

        StringWriter out = new StringWriter();
        save(df, out);
        return out.toString();
    }

    protected void createMissingDirsIfNeeded(File file) {
        if (createMissingDirs) {
            File dir = file.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
        }
    }

    protected void doSave(DataFrame df, Writer out) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonGenerator generator = new JsonFactory(mapper).createGenerator(out);

        // here goes serialization logic... If we ever to make it pluggable, this will need to be turned into a strategy

        Index index = df.getColumnsIndex();
        generator.writeStartArray();
        for (RowProxy r : df) {
            generator.writeStartObject();
            for (String key : index) {
                generator.writeObjectField(key, r.get(key));
            }
            generator.writeEndObject();
        }

        generator.writeEndArray();
        generator.flush();
    }
}
