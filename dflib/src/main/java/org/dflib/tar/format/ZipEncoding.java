package org.dflib.tar.format;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * An interface for encoders that do a pretty encoding of ZIP file names. The main reason for defining an own encoding
 * layer comes from the problems with {@link String#getBytes(String) String.getBytes}, which encodes unknown characters
 * as ASCII quotation marks ('?'). Quotation marks are per definition an invalid file name on some operating systems
 * like Windows, which leads to ignored ZIP entries.
 *
 * @since 2.0.0
 */
class ZipEncoding {

    private static final char REPLACEMENT = '?';
    private static final String REPLACEMENT_STRING = String.valueOf(REPLACEMENT);

    private final Charset charset;
    private final boolean useReplacement;

    ZipEncoding(Charset charset) {
        this.charset = charset;
        this.useReplacement = isUTF8(charset);
    }

    public String decode(byte[] data) throws IOException {
        return newDecoder().decode(ByteBuffer.wrap(data)).toString();
    }

    private CharsetDecoder newDecoder() {
        return useReplacement
                ? charset.newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE).replaceWith(REPLACEMENT_STRING)
                : charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
    }

    private static boolean isUTF8(Charset charset) {
        String name = charset.name();
        return UTF_8.name().equalsIgnoreCase(name) || UTF_8.aliases().stream().anyMatch(name::equalsIgnoreCase);
    }

}
