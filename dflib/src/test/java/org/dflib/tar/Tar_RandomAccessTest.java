package org.dflib.tar;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tar_RandomAccessTest {

    @Test
    void ofFile() throws URISyntaxException {
        File tarFile = new File(getClass().getResource("test1.tar").toURI());
        assertTrue(tarFile.exists());

        Tar tar = Tar.of(tarFile);
        assertTrue(tar instanceof RandomAccessTar);
    }

    @Test
    void ofFileName() throws URISyntaxException {
        File tarFile = new File(getClass().getResource("test1.tar").toURI());
        Tar tar = Tar.of(tarFile.getAbsolutePath());
        assertTrue(tar instanceof RandomAccessTar);
    }

    @Test
    void ofPath() throws URISyntaxException {
        Tar tar = Tar.of(Path.of(getClass().getResource("test1.tar").toURI()));
        assertTrue(tar instanceof RandomAccessTar);
    }
}