package org.dflib.csv.parser;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostBOMInputStreamTest {

    static final byte[] BYTES = new byte[]{
            (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66, (byte) 0x77
    };


    private void assertStream(byte[] streamBytes, int headLen, byte[] expectedBytes) throws IOException {

        // read en bulk
        ByteArrayInputStream d1 = new ByteArrayInputStream(streamBytes);
        byte[] h1 = new byte[headLen];
        assertEquals(headLen, d1.readNBytes(h1, 0, headLen));
        try (PostBOMInputStream s = new PostBOMInputStream(d1, h1, headLen)) {
            byte[] read = s.readAllBytes();
            assertArrayEquals(expectedBytes, read);
        }

        // read byte by byte
        ByteArrayInputStream d2 = new ByteArrayInputStream(streamBytes);
        byte[] h2 = new byte[headLen];
        assertEquals(headLen, d2.readNBytes(h2, 0, headLen));
        try (PostBOMInputStream s = new PostBOMInputStream(d2, h2, headLen)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while(true) {
                int b = s.read();
                if(b == -1) {
                    break;
                }

                out.write(b);
            }

            assertArrayEquals(expectedBytes, out.toByteArray());
        }
    }

    @Test
    void read() throws IOException {
        assertStream(BYTES, 1, BYTES);
        assertStream(BYTES, 2, BYTES);
        assertStream(BYTES, 3, BYTES);
        assertStream(BYTES, 4, BYTES);
        assertStream(BYTES, 5, BYTES);
    }
}
