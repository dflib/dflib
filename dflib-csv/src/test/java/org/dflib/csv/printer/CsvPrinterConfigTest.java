package org.dflib.csv.printer;

import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CsvPrinterConfigTest {

    @Test
    public void defaults() {
        CsvPrinterConfig config = CsvPrinterConfig.builder().build();
        assertNotNull(config.csvFormat());
        assertEquals(Charset.defaultCharset(), config.encoding());
        assertNull(config.compressionCodec());
        assertTrue(config.printHeader());
    }

    @Test
    public void builder() {
        CsvFormat fmt = CsvFormat.defaultFormat().delimiter(";").build();
        CsvPrinterConfig config = CsvPrinterConfig.builder()
                .csvFormat(fmt)
                .encoding(StandardCharsets.UTF_16)
                .compressionCodec(Codec.GZIP)
                .printHeader(false)
                .build();

        assertSame(fmt, config.csvFormat());
        assertEquals(StandardCharsets.UTF_16, config.encoding());
        assertEquals(Codec.GZIP, config.compressionCodec());
        assertFalse(config.printHeader());
    }

    @Test
    public void builder_nulls() {
        CsvPrinterConfig.Builder builder = CsvPrinterConfig.builder();
        assertThrows(NullPointerException.class, () -> builder.csvFormat(null));
        assertThrows(NullPointerException.class, () -> builder.encoding(null));
    }

    @Test
    public void buildWithCodec() {
        CsvPrinterConfig.Builder builder = CsvPrinterConfig.builder().compressionCodec(Codec.GZIP);

        assertNull(builder.buildWithCodec(null).compressionCodec());
        assertEquals(Codec.GZIP, builder.build().compressionCodec());
    }
}
