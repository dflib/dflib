package org.dflib.zip;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Zip_ListTest {

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#one")
    void list(Zip zip) {
        List<String> names = zip.list().stream().map(ZipEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("a/test2.txt", "a/test3.txt", "b/c/test4.txt", "test.txt"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#one")
    void list_IncludeFolders(Zip zip) {
        List<String> names = zip.list(true).stream().map(ZipEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("a/", "a/test2.txt", "a/test3.txt", "b/", "b/c/", "b/c/test4.txt", "test.txt"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#two")
    void list_noHidden(Zip zip) {
        List<String> names = zip.list().stream().map(ZipEntry::getName).sorted().collect(Collectors.toList());
        assertEquals(List.of("test/f1.csv", "test/sub/f2.csv"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#two")
    void list_hidden(Zip zip) {
        List<String> names = zip.includeHidden()
                .list()
                .stream().map(ZipEntry::getName).sorted().collect(Collectors.toList());

        assertEquals(List.of(
                "__MACOSX/._test",
                "__MACOSX/test/._.DS_Store",
                "__MACOSX/test/sub/._f2.csv",
                "test/.DS_Store",
                "test/f1.csv",
                "test/sub/f2.csv"), names);
    }

    @ParameterizedTest
    @MethodSource(value = "org.dflib.zip.TestZips#two")
    void list_hidden_Ext(Zip zip) {
        List<String> names = zip
                .includeHidden()
                .includeExtension("DS_Store")
                .list()
                .stream().map(ZipEntry::getName).sorted().collect(Collectors.toList());

        assertEquals(List.of("__MACOSX/test/._.DS_Store", "test/.DS_Store"), names);
    }

}
