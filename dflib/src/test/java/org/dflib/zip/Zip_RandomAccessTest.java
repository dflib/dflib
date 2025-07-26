package org.dflib.zip;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Zip_RandomAccessTest {

    @Test
    void ofFile() throws URISyntaxException {
        File zipFile = new File(getClass().getResource("test.zip").toURI());
        assertTrue(zipFile.exists());

        Zip zip = Zip.of(zipFile);
        assertTrue(zip instanceof Zip.RandomAccessZip);
    }

    @Test
    void ofFileName() throws URISyntaxException {
        File zipFile = new File(getClass().getResource("test.zip").toURI());
        Zip zip = Zip.of(zipFile.getAbsolutePath());
        assertTrue(zip instanceof Zip.RandomAccessZip);
    }

    @Test
    void ofPath() throws URISyntaxException {
        Zip zip = Zip.of(Path.of(getClass().getResource("test.zip").toURI()));
        assertTrue(zip instanceof Zip.RandomAccessZip);
    }
}
