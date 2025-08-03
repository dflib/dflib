package org.dflib.tar;

import org.dflib.ByteSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tar_SequentialTest {

    @Test
    void ofByteSource() {
        ByteSource source = ByteSource.ofUrl(getClass().getResource("test1.tar"));
        Tar tar = Tar.of(source);
        assertTrue(tar instanceof SequentialTar);
    }
}