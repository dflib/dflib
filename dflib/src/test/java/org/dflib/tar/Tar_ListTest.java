package org.dflib.tar;

import org.dflib.tar.format.TarEntry;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tar_ListTest {

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#one")
    void list(Tar tar) {
        List<String> names = tar.list().stream().map(TarEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("a/test2.txt", "a/test3.txt", "b/c/test4.txt", "test.txt"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#one")
    void list_IncludeFolders(Tar tar) {
        List<String> names = tar.list(true).stream().map(TarEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("a/", "a/test2.txt", "a/test3.txt", "b/", "b/c/", "b/c/test4.txt", "test.txt"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#two")
    void list_noHidden(Tar tar) {
        List<String> names = tar.list().stream().map(TarEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("./f1.csv", "./sub/f2.csv"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#two")
    void list_hidden(Tar tar) {
        List<String> names = tar.includeHidden()
                .list()
                .stream().map(TarEntry::getName).sorted().collect(Collectors.toList());

        assertEquals(List.of("./.DS_Store", "./f1.csv", "./sub/f2.csv"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#two")
    void list_hidden_Ext(Tar tar) {
        List<String> names = tar
                .includeHidden()
                .includeExtension("DS_Store")
                .list()
                .stream().map(TarEntry::getName).sorted().collect(Collectors.toList());

        assertEquals(List.of("./.DS_Store"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#one")
    void list_include_predicate(Tar tar) {
        List<String> names = tar
                .include(e -> e.getName().contains("test2") || e.getName().contains("test4"))
                .list()
                .stream().map(TarEntry::getName).sorted().collect(Collectors.toList());

        assertEquals(List.of("a/test2.txt", "b/c/test4.txt"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.tar.TestTars#two")
    void list_include_predicate_withHidden(Tar tar) {
        List<String> names = tar
                .includeHidden()
                .include(e -> e.getName().contains(".DS_Store"))
                .list()
                .stream().map(TarEntry::getName).sorted().collect(Collectors.toList());

        assertEquals(List.of("./.DS_Store"), names);
    }
}