package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.codec.Codec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvSaver_SaveTargetsTest {

    public static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    @TempDir
    Path outBase;

    @Test
    public void saveToString() {
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", Csv.saver().saveToString(DF));
    }

    @Test
    public void save_ToFile() throws IOException {
        Path out = outBase.resolve("save_ToFile.csv");
        Csv.saver().save(DF, out.toFile());
        String csv = Files.readString(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }


    @Test
    public void save_ToFilePath() throws IOException {
        Path out = outBase.resolve("save_ToFilePath.csv");
        Csv.saver().save(DF, out.toFile().getAbsolutePath());
        String csv = Files.readString(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @Test
    public void save_ToPath() throws IOException {
        Path out = outBase.resolve("save_ToPath.csv");
        Csv.saver().save(DF, out);
        String csv = Files.readString(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @Test
    public void save_ToWriter() {
        StringWriter out = new StringWriter();
        Csv.saver().save(DF, out);
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", out.toString());
    }

    @Test
    public void save_ToOutputStream() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Csv.saver().save(DF, out);
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", out.toString());
    }

    @Test
    public void save_ToPath_NoMkdirs() {
        Path out = outBase.resolve("no_such_dir").resolve("save_ToPath_NoMkdirs.csv");
        assertThrows(RuntimeException.class, () -> Csv.saver().save(DF, out));
    }

    @Test
    public void save_ToPath_Mkdirs() throws IOException {
        Path out = outBase.resolve("no_such_dir").resolve("save_ToPath_Mkdirs.csv");
        Csv.saver().createMissingDirs().save(DF, out);
        String csv = Files.readString(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @Test
    public void save_ToOutputStream_ClosesStream() {
        boolean[] closed = {false};
        OutputStream out = new ByteArrayOutputStream() {
            @Override
            public void close() {
                closed[0] = true;
            }
        };

        Csv.saver().save(DF, out);
        assertTrue(closed[0]);
    }

    @Test
    public void save_ToOutputStream_ClosesStreamOnCodecError() {
        boolean[] closed = {false};
        OutputStream out = new ByteArrayOutputStream() {
            @Override
            public void close() {
                closed[0] = true;
            }
        };

        Codec failing = new Codec() {
            @Override
            public ByteSource decompress(ByteSource compressed) {
                return compressed;
            }

            @Override
            public OutputStream compress(OutputStream uncompressed) {
                throw new UncheckedIOException(new IOException("test"));
            }
        };

        assertThrows(UncheckedIOException.class, () -> Csv.saver().compression(failing).save(DF, out));
        assertTrue(closed[0]);
    }

    @Test
    public void save_ToOutputStream_IOError() {
        OutputStream failing = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("test");
            }
        };

        RuntimeException e = assertThrows(RuntimeException.class, () -> Csv.saver().save(DF, failing));
        assertInstanceOf(IOException.class, e.getCause());
    }

    @Test
    public void save_ToAppendable_IOError() {
        Appendable failing = new Appendable() {
            @Override
            public Appendable append(CharSequence csq) throws IOException {
                throw new IOException("test");
            }

            @Override
            public Appendable append(CharSequence csq, int start, int end) throws IOException {
                throw new IOException("test");
            }

            @Override
            public Appendable append(char c) throws IOException {
                throw new IOException("test");
            }
        };

        RuntimeException e = assertThrows(RuntimeException.class, () -> Csv.saver().save(DF, failing));
        assertInstanceOf(IOException.class, e.getCause());
    }

    @Test
    public void save_ToFile_IOErrorMentionsFile() {
        // a directory can't be opened as a file
        RuntimeException e = assertThrows(RuntimeException.class, () -> Csv.saver().save(DF, outBase));
        assertInstanceOf(IOException.class, e.getCause());
        assertTrue(e.getMessage().contains(outBase.toString()), e.getMessage());
    }
}
