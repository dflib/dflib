package org.dflib.zip;

import org.dflib.ByteSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

class TestZips {

    public static List<Zip> one() throws URISyntaxException {
        return List.of(
                Zip.of(ByteSource.ofUrl(Zip_SourcesTest.class.getResource("test.zip"))),
                Zip.of(Path.of(Zip_SourcesTest.class.getResource("test.zip").toURI()))
        );
    }
}
