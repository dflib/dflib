package org.dflib.avro;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AvroSaverSaveTargetsTest {

    @TempDir
    Path outBase;

    @Test
    public void save_ToStream() {

        DataFrame df = DataFrame.byArrayRow("a", "b")
                .appender()
                .append(1, 2)
                .append(3, 4)
                .toDataFrame();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Avro.save(df, out);
        DataFrame loaded = Avro.load(out.toByteArray());

        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void save_ToPath() {

        DataFrame df = DataFrame.byArrayRow("a", "b")
                .appender()
                .append(1, 2)
                .append(3, 4)
                .toDataFrame();

        Path out = outBase.resolve("save_ToPath.csv");
        Avro.save(df, out);
        DataFrame loaded = load(out);

        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void save_ToFile() {

        DataFrame df = DataFrame.byArrayRow("a", "b")
                .appender()
                .append(1, 2)
                .append(3, 4)
                .toDataFrame();

        Path out = outBase.resolve("save_ToFile.csv");
        Avro.save(df, out.toFile());
        DataFrame loaded = load(out);

        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    @Test
    public void save_ToFilePath() {

        DataFrame df = DataFrame.byArrayRow("a", "b")
                .appender()
                .append(1, 2)
                .append(3, 4)
                .toDataFrame();

        Path out = outBase.resolve("save_ToFilePath.csv");
        Avro.save(df, out.toFile().getAbsolutePath());
        DataFrame loaded = load(out);

        new DataFrameAsserts(loaded, df.getColumnsIndex())
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, 4);
    }

    // as we have no way to check the correctness of the saved files independently of DFLib, let's load
    // the data back to a DataFrame via a single API (byte[]) and compare to the DataFrame
    // TODO: load to a generic structure directly via Avro client?
    private DataFrame load(Path path) {
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Avro.load(bytes);
    }
}
