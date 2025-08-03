package org.dflib.tar.format;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TarFileTest {

    @TempDir
    static Path tempDir;

    private Path getPath(String resource) {
        try {
            return Path.of(getClass().getResource(resource).toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void compress197() throws IOException {
        try (TarFile tarFile = new TarFile(getPath("COMPRESS-197.tar"))) {
            // noop
        }
    }

    @Test
    public void compress657() throws IOException {
        Path tempTar = tempDir.resolve("COMPRESS-657.tar");
        try (GZIPInputStream gin = new GZIPInputStream(Files.newInputStream(getPath("COMPRESS-657.tar.gz")))) {
            Files.copy(gin, tempTar);
        }

        try (TarFile tarFile = new TarFile(tempTar)) {
            for (TarEntry entry : tarFile.getEntries()) {
                if (entry.isDirectory()) {
                    assertFalse(entry.isFile(), "Entry '" + entry.getName() + "' is both a directory and a file");
                }
            }
        }
    }

    @Test
    public void multiByteReadConsistentlyReturnsMinusOneAtEof() throws Exception {
        byte[] buf = new byte[2];
        try (TarFile tarFile = new TarFile(getPath("bla.tar"));
             InputStream input = tarFile.getInputStream(tarFile.getEntries().get(0))) {

            while (input.read() != -1) {
                // read to the end and discard
            }
            assertEquals(-1, input.read(buf));
            assertEquals(-1, input.read(buf));
        }
    }

    @Test
    public void parseTarTruncatedInContent() {
        assertThrows(IOException.class, () -> new TarFile(getPath("COMPRESS-544_truncated_in_content.tar")));
    }

    @Test
    public void parseTarTruncatedInPadding() {
        assertThrows(IOException.class, () -> new TarFile(getPath("COMPRESS-544_truncated_in_padding.tar")));
    }

    @Test
    public void parseTarWithNonNumberPaxHeaders() {
        assertThrows(IOException.class, () -> new TarFile(getPath("COMPRESS-529.tar")));
    }

    @Test
    public void parseTarWithSpecialPaxHeaders() {
        assertThrows(IOException.class, () -> new TarFile(getPath("COMPRESS-530.tar")));
    }

    @Test
    public void readsArchiveCompletely_COMPRESS245() throws IOException {

        Path tempTar = tempDir.resolve("COMPRESS-245.tar");
        try (GZIPInputStream gin = new GZIPInputStream(Files.newInputStream(getPath("COMPRESS-245.tar.gz")))) {
            Files.copy(gin, tempTar);
        }

        try (TarFile tarFile = new TarFile(tempTar)) {
            assertEquals(31, tarFile.getEntries().size());
        }
    }

    @Test
    public void rejectsArchivesWithNegativeSizes() {
        assertThrows(IOException.class, () -> new TarFile(getPath("COMPRESS-569.tar")));
    }

    @Test
    public void shouldReadGNULongNameEntryWithWrongName() throws Exception {
        try (TarFile tarFile = new TarFile(getPath("COMPRESS-324.tar"))) {
            final List<TarEntry> entries = tarFile.getEntries();
            assertEquals(
                    "1234567890123456789012345678901234567890123456789012345678901234567890"
                            + "1234567890123456789012345678901234567890123456789012345678901234567890"
                            + "1234567890123456789012345678901234567890123456789012345678901234567890" + "1234567890123456789012345678901234567890.txt",
                    entries.get(0).getName());
        }
    }

    @Test
    public void shouldThrowAnExceptionOnTruncatedEntries() {
        assertThrows(IOException.class, () -> new TarFile(getPath("COMPRESS-279.tar")));
    }

    @Test
    public void singleByteReadConsistentlyReturnsMinusOneAtEof() throws Exception {
        try (TarFile tarFile = new TarFile(getPath("bla.tar"));
             InputStream input = tarFile.getInputStream(tarFile.getEntries().get(0))) {
            while (input.read() != -1) {
                // read to the end and discard
            }
            assertEquals(-1, input.read());
            assertEquals(-1, input.read());
        }
    }

    @Test
    public void skipsDevNumbersWhenEntryIsNoDevice() throws Exception {
        try (TarFile tarFile = new TarFile(getPath("COMPRESS-417.tar"))) {
            List<TarEntry> entries = tarFile.getEntries();
            assertEquals(2, entries.size());
            assertEquals("test1.xml", entries.get(0).getName());
            assertEquals(TarConstants.LF_NORMAL, entries.get(0).getLinkFlag());
            assertEquals("test2.xml", entries.get(1).getName());
            assertEquals(TarConstants.LF_NORMAL, entries.get(1).getLinkFlag());
        }
    }

    @Test
    public void survivesBlankLinesInPaxHeader() throws Exception {
        try (TarFile tarFile = new TarFile(getPath("COMPRESS-355.tar"))) {
            List<TarEntry> entries = tarFile.getEntries();
            assertEquals(1, entries.size());
            assertEquals("package/package.json", entries.get(0).getName());
            assertEquals(TarConstants.LF_NORMAL, entries.get(0).getLinkFlag());
        }
    }

    @Test
    public void survivesPaxHeaderWithNameEndingInSlash() throws Exception {
        try (TarFile tarFile = new TarFile(getPath("COMPRESS-356.tar"))) {
            final List<TarEntry> entries = tarFile.getEntries();
            assertEquals(1, entries.size());
            assertEquals("package/package.json", entries.get(0).getName());
            assertEquals(TarConstants.LF_NORMAL, entries.get(0).getLinkFlag());
        }
    }

    @Test
    public void throwException() {
        assertThrows(IOException.class, () -> new TarFile(getPath("COMPRESS-553-fail.tar")));
    }

    @Test
    public void throwExceptionWithNullEntry() {
        assertThrows(IOException.class, () -> new TarFile(getPath("COMPRESS-554-fail.tar")));
    }

    @Test
    public void workaroundForBrokenTimeHeader() throws IOException {
        try (TarFile tarFile = new TarFile(getPath("simple-aix-native-tar.tar"))) {
            List<TarEntry> entries = tarFile.getEntries();
            assertEquals(3, entries.size());
            TarEntry entry = entries.get(1);
            assertEquals("sample/link-to-txt-file.lnk", entry.getName());
            assertEquals(TarConstants.LF_SYMLINK, entry.getLinkFlag());
            assertTrue(entry.isSymbolicLink());
        }
    }
}
