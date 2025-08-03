package org.dflib.tar;

import org.dflib.ByteSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Tar_SourceTest {

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#one")
    void source(Tar tar) {
        ByteSource src = tar.source("a/test2.txt");
        assertEquals("a/test2.txt", src.uri().orElse(null));
        assertEquals("test 2 file contents", new String(src.asBytes()));
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#one")
    void source_Invalid(Tar tar) {
        assertThrows(RuntimeException.class, () -> tar.source("no-such-file.txt").asBytes());
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#one")
    void source_folder_Invalid(Tar tar) {
        assertThrows(RuntimeException.class, () -> tar.source("a/").asBytes());
    }
}