package org.dflib.zip;

import org.dflib.ByteSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Zip_SourceTest {

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#one")
    void source(Zip zip) {
        ByteSource src = zip.source("a/test2.txt");
        assertEquals("a/test2.txt", src.uri().orElse(null));
        assertEquals("test 2 file contents", new String(src.asBytes()));
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#one")
    void source_Invalid(Zip zip) {
        assertThrows(RuntimeException.class, () -> zip.source("no-such-file.txt").asBytes());
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#one")
    void source_folder_Invalid(Zip zip) {
        assertThrows(RuntimeException.class, () -> zip.source("a/").asBytes());
    }
}
