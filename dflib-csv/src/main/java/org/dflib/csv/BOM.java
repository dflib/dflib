package org.dflib.csv;

import org.dflib.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

class BOM {

    // if we start supporting UTF-32, this should become 4
    private static final int MAX_BYTES = 3;

    final byte[] bytes;
    final int len;
    final Charset charset;
    final boolean isBom;

    private BOM(byte[] bytes, int len, Charset charset, boolean isBom) {
        this.bytes = Objects.requireNonNull(bytes);
        this.len = len;
        this.charset = Objects.requireNonNull(charset);
        this.isBom = isBom;
    }

    public static Reader reader(ByteSource src, Charset forceEncoding) throws IOException {
        InputStream in = src.stream();

        BOM bom = BOM.check(in);

        // If we found a BOM, BOM bytes are discarded, and we can continue with the original stream.
        // If we didn't, we need to push them back to the stream.

        InputStream finalIn = bom.isBom || bom.len <= 0
                ? in
                : new PostBOMInputStream(in, bom.bytes, bom.len);

        Charset encoding = forceEncoding != null ? forceEncoding : bom.charset;
        return new InputStreamReader(finalIn, encoding);
    }

    public static BOM check(InputStream in) throws IOException {

        byte[] bytes = new byte[MAX_BYTES];
        int b0 = in.read();

        // end of stream
        if (b0 == -1) {
            return new BOM(bytes, 0, Charset.defaultCharset(), false);
        }

        bytes[0] = (byte) b0;
        return switch (b0) {
            // UTF-8
            case 0xEF -> checkUTF8(in, bytes);

            // UTF-16BE
            case 0xFE -> checkUTF16BE(in, bytes);

            // UTF-16LE
            case 0xFF -> checkUTF16LE(in, bytes);

            // TODO: ignoring UTF-32 for now. Those seem very rare

            // no BOM
            default -> new BOM(bytes, 1, Charset.defaultCharset(), false);
        };
    }

    private static BOM checkUTF8(InputStream in, byte[] bytes) throws IOException {

        int read = in.readNBytes(bytes, 1, 2);

        // Important: since we are reading into the byte[], comparison requires a cast on byte constants.
        // Comparing to int will be incorrect
        return read == 2 && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF
                ? new BOM(bytes, 3, StandardCharsets.UTF_8, true)
                : new BOM(bytes, read + 1, Charset.defaultCharset(), false);
    }

    private static BOM checkUTF16BE(InputStream in, byte[] bytes) throws IOException {

        int b1 = in.read();
        // end of stream
        if (b1 == -1) {
            return new BOM(bytes, 1, Charset.defaultCharset(), false);
        }

        bytes[1] = (byte) b1;
        return b1 == 0xFF
                ? new BOM(bytes, 2, StandardCharsets.UTF_16BE, true)
                : new BOM(bytes, 2, Charset.defaultCharset(), false);
    }

    private static BOM checkUTF16LE(InputStream in, byte[] bytes) throws IOException {

        int b1 = in.read();
        // end of stream
        if (b1 == -1) {
            return new BOM(bytes, 1, Charset.defaultCharset(), false);
        }

        bytes[1] = (byte) b1;
        return b1 == 0xFE
                ? new BOM(bytes, 2, StandardCharsets.UTF_16LE, true)
                : new BOM(bytes, 2, Charset.defaultCharset(), false);
    }
}
