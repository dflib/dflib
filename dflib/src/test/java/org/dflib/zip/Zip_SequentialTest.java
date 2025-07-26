package org.dflib.zip;

import org.dflib.ByteSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Zip_SequentialTest {

    @Test
    void of() {
        Zip zip = Zip.of(ByteSource.ofUrl(getClass().getResource("test.zip")));

        // TODO: this will work until DFLib becomes smart enough to distinguish file-based URLs
        //  then we'll need to switch to something like a web URL to produce a sequential access source
        assertTrue(zip instanceof Zip.SequentialZip);
    }
}
