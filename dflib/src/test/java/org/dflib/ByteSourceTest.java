package org.dflib;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteSourceTest {

    @Test
    void of_bytes() {
        byte[] b = "abcd".getBytes();
        assertArrayEquals(b, ByteSource.of(b).asBytes());
    }

    @Test
    void unzip() {
        ByteSources srcs = ByteSource.ofUrl(getClass().getResource("zip/test.zip")).unzip();

        Map<String, String> texts = new HashMap<>();
        srcs.processStreams((n, st) -> {
            try {
                return texts.put(n, new String(st.readAllBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // directories must be skipped
        assertEquals(Map.of(
                "test.txt", "test file contents",
                "a/test3.txt", "test 3 file contents",
                "a/test2.txt", "test 2 file contents",
                "b/c/test4.txt", "test 4 file contents"), texts);
    }
}
