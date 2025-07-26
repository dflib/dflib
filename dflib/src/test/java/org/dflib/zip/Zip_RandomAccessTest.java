package org.dflib.zip;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Zip_RandomAccessTest {

    @Test
    void ofFile() throws URISyntaxException {
        File zipFile = new File(getClass().getResource("test1.zip").toURI());
        assertTrue(zipFile.exists());

        Zip zip = Zip.of(zipFile);
        assertTrue(zip instanceof RandomAccessZip);
    }

    @Test
    void ofFileName() throws URISyntaxException {
        File zipFile = new File(getClass().getResource("test1.zip").toURI());
        Zip zip = Zip.of(zipFile.getAbsolutePath());
        assertTrue(zip instanceof RandomAccessZip);
    }

    @Test
    void ofPath() throws URISyntaxException {
        Zip zip = Zip.of(Path.of(getClass().getResource("test1.zip").toURI()));
        assertTrue(zip instanceof RandomAccessZip);
    }
}
