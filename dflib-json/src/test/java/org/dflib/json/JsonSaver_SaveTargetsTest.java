package org.dflib.json;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonSaver_SaveTargetsTest {

    @TempDir
    Path OUT_BASE;

    @Test
    public void saveToAppendable() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "B",
                2, "C",
                3, "D");

        StringBuffer appendable = new StringBuffer();

        new JsonSaver().save(df, appendable);
        assertEquals("[{\"a\":1,\"b\":\"B\"}," +
                "{\"a\":2,\"b\":\"C\"}," +
                "{\"a\":3,\"b\":\"D\"}]", appendable.toString());
    }

    @Test
    public void saveToString() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "B",
                2, "C",
                3, "D");

        String json = new JsonSaver().saveToString(df);
        assertEquals("[{\"a\":1,\"b\":\"B\"}," +
                "{\"a\":2,\"b\":\"C\"}," +
                "{\"a\":3,\"b\":\"D\"}]", json);
    }

    @Test
    public void saveToPath() throws IOException {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "B",
                2, "C",
                3, "D");

        Path out = OUT_BASE.resolve("saveToPath.json");
        new JsonSaver().save(df, out);

        String json = Files.readString(out);

        assertEquals("[{\"a\":1,\"b\":\"B\"}," +
                "{\"a\":2,\"b\":\"C\"}," +
                "{\"a\":3,\"b\":\"D\"}]", json);
    }

    @Test
    public void saveToFile() throws IOException {

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "B",
                2, "C",
                3, "D");

        Path out = OUT_BASE.resolve("saveToPath.json");
        new JsonSaver().save(df, out.toFile());

        String json = Files.readString(out);

        assertEquals("[{\"a\":1,\"b\":\"B\"}," +
                "{\"a\":2,\"b\":\"C\"}," +
                "{\"a\":3,\"b\":\"D\"}]", json);
    }
}
