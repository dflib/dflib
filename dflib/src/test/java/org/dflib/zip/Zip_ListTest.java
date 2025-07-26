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
        assertEquals(List.of("a/", "a/test2.txt", "a/test3.txt", "b/", "b/c/", "b/c/test4.txt", "test.txt"), names);
    }
}
