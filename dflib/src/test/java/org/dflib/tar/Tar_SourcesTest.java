package org.dflib.tar;

import org.dflib.ByteSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Tar_SourcesTest {

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#one")
    void sources(Tar tar) {
        ByteSources srcs = tar.sources();
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
    @MethodSource(value = "org.dflib.tar.TestTars#one")
    void sourcesReentrant(Tar tar) {

        ByteSources srcs = tar.sources();

        for (int i = 0; i < 5; i++) {
            Map<String, String> labeledTexts = srcs.process((n, s) -> s.uri().orElse("") + ":" + new String(s.asBytes()));

            // directories must be skipped
            assertEquals(Map.of(
                    "test.txt", "test.txt:test file contents",
                    "a/test3.txt", "a/test3.txt:test 3 file contents",
                    "a/test2.txt", "a/test2.txt:test 2 file contents",
                    "b/c/test4.txt", "b/c/test4.txt:test 4 file contents"), labeledTexts);
        }
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#two")
    void sources_noHidden(Tar tar) {

        // capturing both source names and contents
        Map<String, String> processed = tar
                .sources()
                .process((n, s) -> s.uri().orElse("") + ":" + new String(s.asBytes()));

        // directories must be skipped
        assertEquals(Map.of(
                "./f1.csv", "./f1.csv:A,B\n1,s1\n4,s2",
                "./sub/f2.csv", "./sub/f2.csv:C,D\n1,s1\n4,s2"), processed);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#two")
    void sources_hidden_ext(Tar tar) {
        Map<String, String> processed = tar.includeHidden().includeExtension("DS_Store")
                .sources()
                .process((n, s) -> "");

        assertEquals(
                List.of("./.DS_Store"),
                processed.keySet().stream().sorted().collect(Collectors.toList()));
    }
}