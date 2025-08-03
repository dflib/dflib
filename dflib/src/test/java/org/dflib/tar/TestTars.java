package org.dflib.tar;

import org.dflib.ByteSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

class TestTars {

    public static List<Tar> one() throws URISyntaxException {
        return List.of(
                Tar.of(ByteSource.ofUrl(TestTars.class.getResource("test1.tar"))),
                Tar.of(Path.of(TestTars.class.getResource("test1.tar").toURI()))
        );
    }

    public static List<Tar> two() throws URISyntaxException {
        return List.of(
                Tar.of(ByteSource.ofUrl(TestTars.class.getResource("test2.tar"))),
                Tar.of(Path.of(TestTars.class.getResource("test2.tar").toURI()))
        );
    }
}