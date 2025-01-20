package org.dflib.csv;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BOMTest {

    static final byte[] JUST_SOME_BYTES = new byte[]{
            (byte) 0x33, (byte) 0x44, (byte) 0x55, (byte) 0x66
    };

    static final byte[] UTF8_PARTIAL_BYTES = new byte[]{
            (byte) 0xEF, (byte) 0xBB
    };

    static final byte[] UTF16BE_PARTIAL_BYTES = new byte[]{
            (byte) 0xFE, (byte) 0xBB
    };

    static final byte[] UTF8_BYTES = new byte[]{
            (byte) 0xEF, (byte) 0xBB, (byte) 0xBF,
    };

    static final byte[] UTF8_PLUS_BYTES = new byte[]{
            (byte) 0xEF, (byte) 0xBB, (byte) 0xBF, (byte) 0x33, (byte) 0x55
    };

    static final byte[] UTF16BE_BYTES = new byte[]{
            (byte) 0xFE, (byte) 0xFF
    };

    static final byte[] UTF16BE_PLUS_BYTES = new byte[]{
            (byte) 0xFE, (byte) 0xFF, (byte) 0x33, (byte) 0x55
    };

    static final byte[] UTF16LE_BYTES = new byte[]{
            (byte) 0xFF, (byte) 0xFE
    };

    static final byte[] UTF16LE_PLUS_BYTES = new byte[]{
            (byte) 0xFF, (byte) 0xFE, (byte) 0x33, (byte) 0x55
    };

    private void assertBOM(BOM bom, byte[] expectedBytes, int expectedLen, boolean expectedIsBom, Charset expectedCharset) {
        assertEquals(expectedIsBom, bom.isBom);
        assertEquals(expectedLen, bom.len);
        assertArrayEquals(expectedBytes, bom.bytes);
        assertEquals(expectedCharset, bom.charset);
    }

    @Test
    void checkNonBom() throws IOException {
        ByteArrayInputStream justSomeBytes = new ByteArrayInputStream(JUST_SOME_BYTES);
        assertBOM(
                BOM.check(justSomeBytes),
                new byte[]{(byte) 0x33, 0, 0},
                1,
                false,
                Charset.defaultCharset());

        ByteArrayInputStream utf8Partial = new ByteArrayInputStream(UTF8_PARTIAL_BYTES);
        assertBOM(
                BOM.check(utf8Partial),
                new byte[]{(byte) 0xEF, (byte) 0xBB, 0},
                2,
                false,
                Charset.defaultCharset());

        ByteArrayInputStream utf16bePartial = new ByteArrayInputStream(UTF16BE_PARTIAL_BYTES);
        assertBOM(
                BOM.check(utf16bePartial),
                new byte[]{(byte) 0xFE, (byte) 0xBB, 0},
                2,
                false,
                Charset.defaultCharset());
    }

    @Test
    void checkUTF8() throws IOException {
        for (byte[] b : asList(UTF8_BYTES, UTF8_PLUS_BYTES)) {
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            assertBOM(BOM.check(in), UTF8_BYTES, 3, true, StandardCharsets.UTF_8);
        }
    }

    @Test
    void checkUTF16BE() throws IOException {
        for (byte[] b : asList(UTF16BE_BYTES, UTF16BE_PLUS_BYTES)) {
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            assertBOM(
                    BOM.check(in),
                    new byte[]{(byte) 0xFE, (byte) 0xFF, 0},
                    2,
                    true,
                    StandardCharsets.UTF_16BE);
        }
    }

    @Test
    void checkUTF16LE() throws IOException {
        for (byte[] b : asList(UTF16LE_BYTES, UTF16LE_PLUS_BYTES)) {
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            assertBOM(
                    BOM.check(in),
                    new byte[]{(byte) 0xFF, (byte) 0xFE, 0},
                    2,
                    true,
                    StandardCharsets.UTF_16LE);
        }
    }
}
