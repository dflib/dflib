package org.dflib.zip;

import org.junit.jupiter.api.Test;

import java.util.function.Predicate;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZipTest {

    @Test
    void notHidden() {
        Predicate<ZipEntry> filter = Zip.notHiddenFilter();
        assertTrue(filter.test(new ZipEntry("")));
        assertTrue(filter.test(new ZipEntry("a")));
        assertTrue(filter.test(new ZipEntry("a/")));
        assertTrue(filter.test(new ZipEntry("a/b/c")));

        assertFalse(filter.test(new ZipEntry(".")));
        assertFalse(filter.test(new ZipEntry(".a")));
        assertFalse(filter.test(new ZipEntry(".a/")));
        assertFalse(filter.test(new ZipEntry(".b/a/")));
        assertFalse(filter.test(new ZipEntry(".b/a/c")));
        assertFalse(filter.test(new ZipEntry("b/.a")));
        assertFalse(filter.test(new ZipEntry("b/.a/")));
    }
}
