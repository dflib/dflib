package org.dflib.zip;

import org.junit.jupiter.api.Test;

import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZipTest {

    @Test
    void notHidden() {
        assertTrue(Zip.notHidden(new ZipEntry("")));
        assertTrue(Zip.notHidden(new ZipEntry("a")));
        assertTrue(Zip.notHidden(new ZipEntry("a/")));
        assertTrue(Zip.notHidden(new ZipEntry("a/b/c")));

        assertFalse(Zip.notHidden(new ZipEntry(".")));
        assertFalse(Zip.notHidden(new ZipEntry(".a")));
        assertFalse(Zip.notHidden(new ZipEntry(".a/")));
        assertFalse(Zip.notHidden(new ZipEntry(".b/a/")));
        assertFalse(Zip.notHidden(new ZipEntry(".b/a/c")));
        assertFalse(Zip.notHidden(new ZipEntry("b/.a")));
        assertFalse(Zip.notHidden(new ZipEntry("b/.a/")));
    }
}
