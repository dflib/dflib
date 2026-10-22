package org.dflib.s3;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class S3KeysTest {

    @Test
    void relativize() {
        assertEquals("a.csv", S3Keys.relativize("p/", "p/a.csv"));
        assertEquals("sub/a.csv", S3Keys.relativize("p/", "p/sub/a.csv"));
        assertEquals("p/a.csv", S3Keys.relativize("", "p/a.csv"));
        assertThrows(IllegalArgumentException.class, () -> S3Keys.relativize("p/", "x/a.csv"));
    }

    @Test
    void impliedFolders() {
        assertEquals(List.of(), S3Keys.impliedFolders("p/", "p/c.csv"));
        assertEquals(List.of("p/a/", "p/a/b/"), S3Keys.impliedFolders("p/", "p/a/b/c.csv"));
        assertEquals(List.of("p/", "p/a/"), S3Keys.impliedFolders("", "p/a/c.csv"));

        // a folder marker does not imply itself
        assertEquals(List.of("p/a/"), S3Keys.impliedFolders("p/", "p/a/b/"));
    }

    @Test
    void isHidden() {
        assertFalse(S3Keys.isHidden("p/", "p/a.csv"));
        assertFalse(S3Keys.isHidden("p/", "p/a/b.csv"));
        assertFalse(S3Keys.isHidden("p/", "p/a.b/c.csv"));
        assertTrue(S3Keys.isHidden("p/", "p/.a.csv"));
        assertTrue(S3Keys.isHidden("p/", "p/a/.b/c.csv"));
        assertTrue(S3Keys.isHidden("p/", "p/.git/"));

        // the folder itself may be hidden, but its contents are not considered hidden
        assertFalse(S3Keys.isHidden(".cache/", ".cache/a.csv"));
    }

    @Test
    void appendPath() {
        assertEquals("p/a/", S3Keys.appendPath("p/", "a"));
        assertEquals("p/a/", S3Keys.appendPath("p/", "a/"));
        assertEquals("a/", S3Keys.appendPath("", "a"));
        assertEquals("p/", S3Keys.appendPath("p/", ""));
        assertThrows(IllegalArgumentException.class, () -> S3Keys.appendPath("p/", "/a"));
    }
}
