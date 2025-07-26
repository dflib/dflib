package org.dflib.zip;

import org.dflib.ByteSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Zip_SourcesTest {

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#one")
    void sources(Zip zip) {
        ByteSources srcs = zip.sources();
        assertNotNull(srcs);

        // capturing both source names and contents
        Map<String, String> labeledTexts = srcs.process((n, s) -> s.uri().orElse("") + ":" + new String(s.asBytes()));

        // directories must be skipped
        assertEquals(Map.of(
                "test.txt", "test.txt:test file contents",
                "a/test3.txt", "a/test3.txt:test 3 file contents",
                "a/test2.txt", "a/test2.txt:test 2 file contents",
                "b/c/test4.txt", "b/c/test4.txt:test 4 file contents"), labeledTexts);

    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#two")
    void sources_noHidden(Zip zip) {
        ByteSources srcs = zip.sources();
        assertNotNull(srcs);

        // capturing both source names and contents
        Map<String, String> processed = srcs.process((n, s) -> s.uri().orElse("") + ":" + new String(s.asBytes()));

        // directories must be skipped
        assertEquals(Map.of(
                "test/f1.csv", "test/f1.csv:A,B\n1,s1\n4,s2",
                "test/sub/f2.csv", "test/sub/f2.csv:C,D\n1,s1\n4,s2"), processed);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#two")
    void sources_hidden_ext(Zip zip) {
        ByteSources srcs = zip.includeHidden().includeExtension("DS_Store").sources();
        assertNotNull(srcs);

        Map<String, String> processed = srcs.process((n, s) -> "");
        assertEquals(
                List.of("__MACOSX/test/._.DS_Store", "test/.DS_Store"),
                processed.keySet().stream().sorted().collect(Collectors.toList()));
    }
}
